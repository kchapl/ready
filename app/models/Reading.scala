package models

import java.time.LocalDate

sealed trait Reading:
  def book: Book
  def started: LocalDate

object Reading:
  case class InProgress(book: Book, started: LocalDate) extends Reading
  case class Completed(book: Book, started: LocalDate, completed: LocalDate, rating: Int) extends Reading
