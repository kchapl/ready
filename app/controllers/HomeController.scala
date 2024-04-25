package controllers

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import play.api.*
import play.api.mvc.*

/** This controller creates an `Action` to handle HTTP requests to the application's home page.
  */
class HomeController(
    val controllerComponents: ControllerComponents,
    transport: HttpTransport,
    jsonFactory: JsonFactory,
    authCodeFlow: GoogleAuthorizationCodeFlow
) extends BaseController:

  private val redirectUrl = "http://localhost:9000/oauth2callback"

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be called when the application receives a `GET`
    * request with a path of `/`.
    */
  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index())
  }

  def authorize(): Action[AnyContent] = Action { implicit request =>
    Redirect(
      authCodeFlow
        .newAuthorizationUrl()
        .setRedirectUri(
          authCodeFlow.newAuthorizationUrl().setRedirectUri(redirectUrl).build()
        )
        .build()
    )
  }

  def oauth2callback(code: String): Action[AnyContent] = Action { implicit request =>
    val tokenResponse =
      authCodeFlow.newTokenRequest(code).setRedirectUri(redirectUrl).execute()

    val credential = authCodeFlow.createAndStoreCredential(tokenResponse, null)
    val sheetsService = new Sheets.Builder(transport, jsonFactory, credential)
      .setApplicationName("Your App Name")
      .build()

    // Use `sheetsService` to make requests to Google Sheets API

    Ok("Authenticated successfully!")
  }
