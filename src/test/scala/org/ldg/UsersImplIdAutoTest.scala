package org.ldg

import java.util.UUID

import cats.Id
import cats.implicits._
import org.ldg.impl.UsersImpl
import org.scalatest.FunSuiteLike
import org.typelevel.discipline.scalatest.Discipline

/**
  * An implementation of UsersLaws for UserImpl[Id]
  */
class UsersImplIdAutoTest extends Discipline with FunSuiteLike {

  def mkUsersDao() : FakeSqlDocDao[UUID, UsersImpl.UserData] =
    new FakeSqlDocDao[UUID, UsersImpl.UserData] {
      override def findByNativeQuery(sql: String): Id[Seq[Data]] = {
        // hack for lookup by username since native query not supported by FakeSqlDocDao
        if(sql.startsWith("`username`='")) {
          val username = sql.drop("`username`='".length).takeWhile(_ != '\'')
          Option[Data](data.searchValues(1000, new java.util.function.Function[Data,Data] {
            def apply(t: Data): Data = if(t._2.username == username) t else null
          })).toSeq
        } else {
          super.findByNativeQuery(sql)
        }
      }
    }

  // state less
  val fakePasswords = new FakePasswords
  val consoleLogger = new ConsoleLogger

  def mkUsersImpl() : Users[Id] with UsersEfx[Id] = {
    val usersDao = mkUsersDao()
    new UsersImpl[Id](
      usersDao = usersDao,
      passwords = fakePasswords,
      logger = consoleLogger
    ) with UsersEfx[Id] {
      def efx_createUser(testUser: TestUser): Id[Unit] =
        usersDao.insert(testUser.id, UsersImpl.UserData(
          username = testUser.username,
          passwordDigest = fakePasswords.mkDigest(testUser.plainTextPassword)
        ))

      def efx_removeUser(testUser: TestUser): Id[Unit] =
        usersDao.remove(testUser.id)

      def efx_state: Id[List[Users.User]] =
        usersDao.findAll(0,0).map(UsersImpl.toUser).toList
    }
  }

  val usersLaws = new UsersLaws[Id](() => mkUsersImpl())

  checkAll("UsersImpl[Id]", usersLaws.ruleSet)
}