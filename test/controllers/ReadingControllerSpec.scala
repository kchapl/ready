package controllers

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import load.AppComponentsBase
import models.{Book, Reading}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.*
import org.scalatestplus.play.*
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.test.*
import play.api.test.Helpers.*
import services.ReadingStorage

import java.time.LocalDate

class ReadingControllerSpec extends PlaySpec with OneAppPerSuiteWithComponents with MockitoSugar:

  override val components: AppComponentsBase = new AppComponentsBase(context):
    override def authCodeFlow: GoogleAuthorizationCodeFlow = mock[GoogleAuthorizationCodeFlow]

  "showAll" should {

    "render the readingAll page from a new instance of controller" in {
      val readingStorage = mock[ReadingStorage]
      when(readingStorage.fetchAll()).thenReturn(
        Seq(
          Reading.Completed(
            book = Book(isbn = "123", author = "a b", title = "t1"),
            started = LocalDate.of(2023, 1, 2),
            completed = LocalDate.of(2023, 3, 12),
            rating = 3
          )
        )
      )
      val controller = new ReadingController(stubControllerComponents(), readingStorage)
      val result = controller.showAll().apply(FakeRequest(GET, "/readings"))

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Readings")
    }

    "render the readingAll page from the application" in {
      val controller = components.readingController
      val result = controller.showAll().apply(FakeRequest(GET, "/readings"))

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Readings")
    }

    "render the readingAll page from the router" in {
      val request = FakeRequest(GET, "/readings")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Readings")
    }
  }
