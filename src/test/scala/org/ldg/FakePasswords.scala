package org.ldg

import cats.Id

class FakePasswords extends Passwords[Id] {
  def compareDigest(plainTextPassword: String, passwordDigest: String): Id[Boolean] =
    plainTextPassword == passwordDigest
  def mkDigest(plainTextPassword: String): Id[String] = plainTextPassword
}