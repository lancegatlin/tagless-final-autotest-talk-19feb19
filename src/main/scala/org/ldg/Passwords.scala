package org.ldg

import cats.tagless._

@autoFunctorK
trait Passwords[F[_]] {
  def compareDigest(
    plainTextPassword: String,
    passwordDigest: String
  ) : F[Boolean]

  def mkDigest(plainTextPassword: String) : F[String]
}
