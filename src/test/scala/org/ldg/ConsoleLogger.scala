package org.ldg

import java.time.Instant

import cats.Id

/**
  * A basic logger that prints to the console
  */
class ConsoleLogger extends Logger[Id] {
  def log(message: String, cause: Throwable) =
    println(s"[${Instant.now}]$message (cause=$cause)")

  def log(message: String) =
    println(s"[${Instant.now}]$message")

  def trace(message: String): Id[Unit] =
    log(s"[TRACE]: $message")

  def trace2(message: String, cause: Throwable): Id[Unit] =
    log(s"[TRACE]: $message", cause)

  def debug(message: String): Id[Unit] =
    log(s"[DEBUG]: $message")

  def debug2(message: String, cause: Throwable): Id[Unit] =
    log(s"[DEBUG]: $message", cause)

  def info(message: String): Id[Unit] =
    log(s"[INFO]: $message")

  def info2(message: String, cause: Throwable): Id[Unit] =
    log(s"[INFO]: $message", cause)

  def warn(message: String): Id[Unit] =
    log(s"[WARN]: $message")

  def warn2(message: String, cause: Throwable): Id[Unit] =
    log(s"[WARN]: $message",  cause)

  def error(message: String): Id[Unit] =
    log(s"[ERROR]: $message")

  def error2(message: String, cause: Throwable): Id[Unit] =
    log(s"[ERROR]: $message", cause)
}
