package org.ldg

import java.time.Instant

import cats.tagless.autoFunctorK
import org.ldg.SqlDocDao.RecordMetadata

@autoFunctorK
trait SqlDocDao[ID,A,F[_]] {
  def exists(id: ID): F[Boolean]

  def findById(id: ID): F[Option[(ID, A, RecordMetadata)]]

  def findByNativeQuery(sql: String): F[Seq[(ID, A, RecordMetadata)]]

  def findAll(start: Int, batchSize: Int): F[Seq[(ID, A, RecordMetadata)]]

  def insert(id: ID, a: A): F[Boolean]

  def update(id: ID, value: A): F[Boolean]

  def remove(id: ID): F[Boolean]

  // note: added to simplify demo, don't recommend otherwise
  def clear() : F[Unit]
}

object SqlDocDao {
  case class RecordMetadata(
    created: Instant,
    lastUpdated: Instant,
    removed: Option[Instant]
  )
}
