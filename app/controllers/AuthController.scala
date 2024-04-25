package controllers

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class AuthController(val controllerComponents: ControllerComponents, authCodeFlow: GoogleAuthorizationCodeFlow)
    extends BaseController:

  def signIn(): Action[AnyContent] = TODO
  def signOut(): Action[AnyContent] = TODO

  def googleSignIn(): Action[AnyContent] = Action { implicit request =>
    Option(authCodeFlow.loadCredential("abc")) match
      case Some(credential) =>
        Ok(s"Already signed in with ${credential.getAccessToken}")
      case None =>
        val authorizationUrl =
          authCodeFlow.newAuthorizationUrl().setRedirectUri("http://localhost:9000/auth/google/callback").build()
        Redirect(authorizationUrl)
  }

  def googleSignInCallback(code: String): Action[AnyContent] = Action.async { implicit request =>
    val tokenRequest = authCodeFlow.newTokenRequest(code).setRedirectUri("http://localhost:9000/auth/google/callback")
    Try(tokenRequest.execute()) match
      case Success(tokenResponse) =>
        authCodeFlow.createAndStoreCredential(tokenResponse, "abc")
        // Successfully retrieved access token, now fetch user info using the access token
        val accessToken = tokenResponse.getAccessToken
        // Use the access token to make requests to Google APIs, e.g., fetch user profile information
        // For brevity, user profile information retrieval is not included in this example
        Future.successful(Ok("Sign-in successful"))
      case Failure(ex) =>
        // Failed to retrieve access token
        Future.successful(BadRequest("Failed to sign in"))
  }
