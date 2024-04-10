package controllers

import play.api.*
import play.api.mvc.*
import services.ReadingStorage

class ReadingController(val controllerComponents: ControllerComponents, readingStorage: ReadingStorage)
    extends BaseController:
  def showAll() = Action {
    Ok(views.html.readingsAll(readingStorage.fetchAll()))
  }
