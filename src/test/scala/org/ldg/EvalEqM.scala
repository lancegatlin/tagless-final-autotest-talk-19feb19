package org.ldg

import cats.{Eq, Id, Monad}
import cats.implicits._

/**
  * Used by ScalaCheck to evaluate two Monads to compare their contents
  * Note1: required since ScalaCheck is not monad aware
  * Note2: for async code implementing this will require blocking
  * @tparam M
  */
trait EvalEqM[M[_]] {
  def M: Monad[M]
  def eval[A](x: M[A], y: M[A])(implicit eqA: Eq[A]) : Boolean

  implicit def mkEqMA[A](implicit eqA: Eq[A]) : Eq[M[A]] = new Eq[M[A]] {
    def eqv(x: M[A], y: M[A]): Boolean = eval(x,y)(eqA)
  }
}

object EvalEqM {
  implicit val evalEqM : EvalEqM[Id] = new EvalEqM[Id] {
    val M: Monad[Id] = implicitly[Monad[Id]]
    def eval[A](x: Id[A], y: Id[A])(implicit eq: Eq[A]): Boolean =
      eq.eqv(x,y)
  }
}