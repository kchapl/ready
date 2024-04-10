package controllers

import load.AppComponents
import org.scalatestplus.play.*
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.test.*
import play.api.test.Helpers.*

class ReadingControllerSpec extends PlaySpec with OneAppPerSuiteWithComponents:

  override val components: AppComponents = new AppComponents(context)

  "showAll" should {

    "render the readingAll page from a new instance of controller" in {
      val controller = new ReadingController(stubControllerComponents())
      val home = controller.showAll().apply(FakeRequest(GET, "/readings"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Readings")
    }

    "render the readingAll page from the application" in {
      val controller = components.readingController
      val home = controller.showAll().apply(FakeRequest(GET, "/readings"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Readings")
    }

    "render the readingAll page from the router" in {
      val request = FakeRequest(GET, "/readings")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Readings")
    }
  }
