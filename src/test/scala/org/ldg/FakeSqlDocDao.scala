package org.ldg

import java.time.Instant

import cats.Id
import SqlDocDao.RecordMetadata

import scala.collection.JavaConverters._

class FakeSqlDocDao[A,E] extends SqlDocDao[A, E, Id] {
  type Data = (A, E, RecordMetadata)
  val data = new java.util.concurrent.ConcurrentHashMap[A,Data]()

  def exists(id: A): Id[Boolean] =
    data.contains(id)

  def findById(id: A): Id[Option[Data]] =
    Option(data.get(id))

  def findByNativeQuery(sql: String): Id[Seq[Data]] =
    throw new UnsupportedOperationException

  def findAll(start: Int, batchSize: Int): Id[Seq[Data]] =
    data.asScala.valuesIterator.toSeq

  def insert(id: A, a: E): Id[Boolean] = {
    data.putIfAbsent(id, (id, a, RecordMetadata(
      created = Instant.now,
      lastUpdated = Instant.now,
      removed = None
    ))) == null
  }

  def update(id: A, newValue: E): Id[Boolean] =
    data.computeIfPresent(id, new java.util.function.BiFunction[A,Data,Data] {
      def apply(a: A, d: Data): Data = (a,newValue,d._3.copy(
        lastUpdated = Instant.now
      ))
    }) != null

  def remove(id: A): Id[Boolean] =
    data.computeIfPresent(id, new java.util.function.BiFunction[A,Data,Data] {
      def apply(a: A, d: Data): Data = d.copy(_3 = d._3.copy(
        removed = Some(Instant.now)
      ))
    }) != null
}
