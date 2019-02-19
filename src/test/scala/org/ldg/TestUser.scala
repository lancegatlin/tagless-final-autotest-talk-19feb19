package org.ldg

import java.util.UUID

import cats.kernel.Eq
import org.scalacheck._
import org.scalacheck.Gen._

case class TestUser(
  id: UUID,
  username: String,
  plainTextPassword: String
)

object TestUser  {
  val TestUserGen: Gen[TestUser] =
    for {
      username  <- alphaStr suchThat (_.length <= 128)
      plainTextPassword <- alphaStr suchThat (_.length <= 128)
    } yield TestUser(
      id = UUID.randomUUID(),
      username = username,
      plainTextPassword = plainTextPassword
    )

  implicit val Arbitrary_TestUser = Arbitrary(TestUserGen)

  implicit val Eq_TestUser : Eq[TestUser] = Eq.fromUniversalEquals
}
