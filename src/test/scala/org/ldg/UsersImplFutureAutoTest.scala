package org.ldg

import cats.arrow.FunctionK
import cats.{Eq, Id, Monad}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits._
import cats.implicits._

object UsersImplFutureAutoTest {
  implicit val functionK_Id_Future = new FunctionK[Id,Future] {
    def apply[A](fa: Id[A]): Future[A] = Future.successful(fa)
  }


  implicit val evalEqM_Future = new EvalEqM[Future] {
    def M: Monad[Future] = implicitly[Monad[Future]]

    def eval[A](xf: Future[A], yf: Future[A])(implicit eqA: Eq[A]): Boolean =
      Await.result(
        for {
          x <- xf
          y <- yf
        } yield {
          eqA.eqv(x,y)
        },
        Duration.Inf
      )

  }
}

/**
  * An implementation of UsersLaws for UserImpl[Future]
  */
class UsersImplFutureAutoTest extends UsersImplAutoTest[Future]("Future")(
  fk = UsersImplFutureAutoTest.functionK_Id_Future,
  F = implicitly,
  evalEqM = UsersImplFutureAutoTest.evalEqM_Future
)