package org.ldg

import cats.Id
import cats.arrow.FunctionK

/**
  * An implementation of UsersLaws for UserImpl[Id]
  */
class UsersImplIdAutoTest extends UsersImplAutoTest[Id]("Id")(
  fk = new FunctionK[Id,Id] {
    def apply[A](fa: Id[A]): Id[A] = fa
  },
  F = implicitly,
  evalEqM = implicitly
)
// intellij shows erroneous error here