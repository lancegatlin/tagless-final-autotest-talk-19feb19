package org.ldg

import java.time.Instant
import java.util.UUID

import org.typelevel.discipline.Laws
import cats.kernel.laws.discipline._
import org.scalacheck.Prop._
import cats.{Eq, Monad}
import cats.implicits._
import cats.kernel.laws._

/**
  * A set of laws to describe and verify any implementation of the Users
  * algebra/API using any monad F (that can be evaluated in the current thread)
  *
  * @param mkFixture creates a test fixture of some concrete implementation
  *                  of Users that additionally allows certain manipulations
  *                  of the underlying effect system directly
  * @param F monad type-class for F
  * @param evalEqM a type-class that allows testing if two monads contain the
  *                same value by evaluating them in the current thread
  * @tparam F monad type
  */
class UsersLaws[F[_]](
  mkFixture: () => Users[F] with UsersEfx[F]
)(implicit
  F:Monad[F],
  evalEqM: EvalEqM[F]
) extends Laws {
  import evalEqM._

  import cats.syntax.apply._
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  // utils

  implicit val userEq = new Eq[Users.User] {
    def eqv(x: Users.User, y: Users.User): Boolean = {
      // check everything except timestamp
      x.id === y.id &&
      x.username === y.username &&
      x.passwordDigest === y.passwordDigest &&
      x.removed.nonEmpty === y.removed.nonEmpty
    }
  }

  // laws

  def desugared_law_createThenFind(testUser: TestUser) : IsEq[F[Option[Users.User]]] = {
    val users = mkFixture()
    for {
      _ <- users.create(testUser.id, testUser.username, testUser.plainTextPassword)
      user <- users.findById(testUser.id)
    } yield user
  } <-> {
    val users = mkFixture()
    for {
      _ <- users.efx_createUser(testUser)
      user <- users.findById(testUser.id)
    } yield user
  }

  // sugared
  def law_createThenFind(testUser: TestUser) = {
    val users = mkFixture()
    users.create(testUser.id, testUser.username, testUser.plainTextPassword) >>
    users.findById(testUser.id) <->
    F.pure(Some(Users.User(
      id = testUser.id,
      username = testUser.username,
      passwordDigest = testUser.plainTextPassword,
      created = Instant.now,
      removed = None
    )))
  }

  def law_createEfx(testUser: TestUser) = {
    {
      val users = mkFixture()
      users.create(testUser.id, testUser.username, testUser.plainTextPassword) >>
      users.efx_state
    } <-> {
      val users = mkFixture()
      users.efx_createUser(testUser) >>
      users.efx_state
    }
  }

  def law_findMissing(testUser: TestUser) = {
    val users = mkFixture()
    users.findById(testUser.id) <->
    F.pure(None)
  }

  def law_createDisallowDupUsername(testUser: TestUser) = {
    val users = mkFixture()
    users.create(testUser.id, testUser.username, testUser.plainTextPassword) >>
    users.create(UUID.randomUUID(), testUser.username, testUser.plainTextPassword) <->
    F.pure(false)
  }

  def law_createRemoveFind(testUser: TestUser) = {
    val users = mkFixture()
    users.create(testUser.id, testUser.username, testUser.plainTextPassword) >>
    users.remove(testUser.id) >>
    users.findById(testUser.id).map(_.map(_.removed.nonEmpty)) <->
    F.pure(Some(true))
  }

  // todo: Users.findByUsername
  // todo: Users.setPassword

  val ruleSet =
    new SimpleRuleSet(
      name = "Users",
      "create then find should succeed" ->
        // desugared
        forAll { testUser: TestUser =>
          law_createThenFind(testUser)
        },

      // sugared
      "create and efx_createUser should create equivalent state" -> forAll(law_createEfx _),
      "create should disallow dup username" -> forAll(law_createDisallowDupUsername _),
      "find should return None for user that doesn't exist" -> forAll(law_findMissing _),
      "create, remove then find should return user flagged as removed" -> forAll(law_createRemoveFind _),
    )
}
