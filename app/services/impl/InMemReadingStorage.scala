package services.impl

import models.{Book, Reading}
import services.ReadingStorage

import java.time.LocalDate

class InMemReadingStorage extends ReadingStorage:
  override def fetchAll(): Seq[Reading] = Seq(
    Reading.Completed(
      book = Book(isbn = "123", author = "a b", title = "storm"),
      started = LocalDate.of(2023, 1, 2),
      completed = LocalDate.of(2023, 3, 12),
      rating = 3
    )
  )
