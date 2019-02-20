package org.ldg


import cats.arrow.FunctionK
import cats.{Eq, Id, Monad}
import cats.effect.IO
import cats.effect.implicits._
//import cats.implicits._

object UsersImplIOAutoTest {
  implicit val evalEqM_IO = new EvalEqM[IO] {
    def M: Monad[IO] = implicitly

    def eval[A](mx: IO[A], my: IO[A])(implicit eqA: Eq[A]): Boolean = {
      for {
        x <- mx
        y <- my
      } yield eqA.eqv(x,y)
    }.unsafeRunSync()
  }
  val fk_IO = new FunctionK[Id, IO]{
    def apply[A](fa: Id[A]): IO[A] = IO.pure(fa)
  }
}
/**
  * An implementation of UsersLaws for UserImpl[IO]
  */
class UsersImplIOAutoTest extends UsersImplAutoTest[IO]("IO")(
  fk = UsersImplIOAutoTest.fk_IO,
  F = implicitly,
  evalEqM = UsersImplIOAutoTest.evalEqM_IO
)
// intellij shows erroneous error here