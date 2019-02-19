package org.ldg

import cats.Id

/**
  * An implementation of Passwords[Id] that does nothing
  */
class FakePasswords extends Passwords[Id] {
  def compareDigest(plainTextPassword: String, passwordDigest: String): Id[Boolean] =
    plainTextPassword == passwordDigest
  def mkDigest(plainTextPassword: String): Id[String] = plainTextPassword
}