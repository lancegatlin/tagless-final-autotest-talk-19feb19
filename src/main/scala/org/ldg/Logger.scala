package org.ldg

import cats.tagless.autoFunctorK

@autoFunctorK
trait Logger[F[_]] {
  def trace(message:  String) : F[Unit]
  def trace2(message: String, cause: Throwable) : F[Unit]
  def debug(message: String) : F[Unit]
  def debug2(message: String, cause: Throwable) : F[Unit]
  def info(message: String) : F[Unit]
  def info2(message: String, cause: Throwable) : F[Unit]
  def warn(message: String) : F[Unit]
  def warn2(message: String, cause: Throwable) : F[Unit]
  def error(message: String) : F[Unit]
  def error2(message: String, cause: Throwable) : F[Unit]
}
