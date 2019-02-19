package org.ldg

import java.util.UUID

import cats.Id
import cats.implicits._
import org.ldg.impl.UsersImpl
import org.scalatest.FunSuiteLike
import org.typelevel.discipline.scalatest.Discipline

class UsersImplAutoTest extends Discipline with FunSuiteLike {
  def mkUsersDao() : SqlDocDao[UUID, UsersImpl.UserData, Id] =
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

  def mkUsersImpl() = new UsersImpl(
    usersDao = mkUsersDao(),
    passwords = fakePasswords,
    logger = consoleLogger
  )

  checkAll("UsersImpl", new UsersLaws(mkUsersImpl).ruleSet)
}