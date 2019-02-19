package org.ldg

import org.typelevel.discipline.Laws
import cats.kernel.laws.discipline._
import cats.{Eq, Monad}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import cats.Monad
import cats.kernel.laws._

class UsersLaws[F[_]](
  mkUsers: () => Users[F]
)(implicit
  F:Monad[F]
) extends Laws {

  import cats.syntax.apply._
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  def userToTestUser(user: Users.User) : TestUser = {
    TestUser(
      id = user.id,
      username = user.username,
      plainTextPassword = user.passwordDigest
    )
  }

  def createThenFind(testUser: TestUser) : IsEq[F[Option[TestUser]]] = {
    val users = mkUsers()
    for {
      _ <- users.create(testUser.id, testUser.username, testUser.plainTextPassword)
      user <- users.findById(testUser.id)
    } yield {
      user.map(userToTestUser)
    }
  } <-> {
    // todo: create Users, somehow inject test user?
    F.pure(Some(testUser))
  }

  def terse_createThenFind(testUser: TestUser) : IsEq[F[Option[TestUser]]] = {
    val users = mkUsers()
    users.create(testUser.id, testUser.username, testUser.plainTextPassword) >>
    users.findById(testUser.id).map(_.map(userToTestUser)) <->
    F.pure(Some(testUser))
  }

  def createDisallowDupUsername(testUser: TestUser) : IsEq[F[Boolean]] = {
    val users = mkUsers()
    users.create(testUser.id, testUser.username, testUser.plainTextPassword) >>
    users.create(testUser.id, testUser.username, testUser.plainTextPassword) <->
    F.pure(false)
  }

  def ruleSet(implicit
    arbTestUser: Arbitrary[TestUser],
    // todo: need a way to bring F outside Eq to avoid needing to be explicit
    // todo: about every F inner type that might be compared
    eq1: Eq[F[Option[TestUser]]],
    eq2: Eq[F[Boolean]]
  ) =
    new SimpleRuleSet(
      name = "Users",
      "create then find" -> forAll(createThenFind _),
             "create disallow dup username" -> forAll(createDisallowDupUsername _)
    )
}
