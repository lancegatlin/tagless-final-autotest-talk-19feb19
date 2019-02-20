package org.ldg

import java.util.UUID

import cats.{Id, Monad}
import org.ldg.impl.UsersImpl
import org.scalatest.FunSuiteLike
import org.typelevel.discipline.scalatest.Discipline

import cats.tagless.implicits._
import cats._

class UsersImplAutoTest[F[_]](
  monadType: String
)(implicit
  fk: Id ~> F,
  F:Monad[F],
  evalEqM: EvalEqM[F]
) extends Discipline with FunSuiteLike {
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  def mkFakeSqlDocDao() = new FakeSqlDocDao[UUID, UsersImpl.UserData] {
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
  def mkUsersDao() : SqlDocDao[UUID, UsersImpl.UserData,F] =
  // intellij shows erroneous error here
    mkFakeSqlDocDao().mapK(fk)

  // state less
  val fakePasswords : Passwords[F] = (new FakePasswords).mapK(fk)
  val consoleLogger : Logger[F] = (new ConsoleLogger).mapK(fk)

  def mkUsersImpl() : Users[F] with UsersEfx[F] = {
    val usersDao = mkUsersDao()
    new UsersImpl[F](
      usersDao = usersDao,
      passwords = fakePasswords,
      logger = consoleLogger
    ) with UsersEfx[F] {
      def efx_createUser(testUser: TestUser): F[Unit] =
        usersDao.insert(testUser.id, UsersImpl.UserData(
          username = testUser.username,
          passwordDigest = testUser.plainTextPassword
        )).map(_ => ())

      def efx_removeUser(testUser: TestUser): F[Unit] =
        usersDao.remove(testUser.id).map(_ => ())

      def efx_state: F[List[Users.User]] = {
        usersDao.findAll(0, 0).map(_.map(UsersImpl.toUser).toList)
//          .map { retv =>
//          fakeSqlDocDao.data.clear()
//          retv
//        }
      }
    }
  }

  val usersLaws = new UsersLaws[F](() => mkUsersImpl())

  checkAll(s"UsersImpl[$monadType]", usersLaws.ruleSet)
}
