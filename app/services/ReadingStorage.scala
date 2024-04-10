package services

import models.Reading

trait ReadingStorage:
  def fetchAll(): Seq[Reading]
