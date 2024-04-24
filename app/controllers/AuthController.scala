package controllers

import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import java.io.FileReader
import scala.concurrent.Future
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

class AuthController(val controllerComponents: ControllerComponents) extends BaseController:

  private val transport = new NetHttpTransport()

  private val jsonFactory = JacksonFactory.getDefaultInstance

  private val clientSecrets = GoogleClientSecrets.load(
    jsonFactory,
    new FileReader(sys.Prop[String]("GOOGLE_APPLICATION_CREDENTIALS").value)
  )

  private val scopes = Seq("https://www.googleapis.com/auth/userinfo.profile").asJava

  private val flow = new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, scopes)
    .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance)
    .build()

  def signIn(): Action[AnyContent] = TODO
  def signOut(): Action[AnyContent] = TODO

  def googleSignIn(): Action[AnyContent] = Action { implicit request =>
    Option(flow.loadCredential("abc")) match
      case Some(credential) =>
        Ok(s"Already signed in with ${credential.getAccessToken}")
      case None =>
        val authorizationUrl =
          flow.newAuthorizationUrl().setRedirectUri("http://localhost:9000/auth/google/callback").build()
        Redirect(authorizationUrl)
  }

  def googleSignInCallback(code: String): Action[AnyContent] = Action.async { implicit request =>
    val tokenRequest = flow.newTokenRequest(code).setRedirectUri("http://localhost:9000/auth/google/callback")
    Try(tokenRequest.execute()) match
      case Success(tokenResponse) =>
        flow.createAndStoreCredential(tokenResponse, "abc")
        // Successfully retrieved access token, now fetch user info using the access token
        val accessToken = tokenResponse.getAccessToken
        // Use the access token to make requests to Google APIs, e.g., fetch user profile information
        // For brevity, user profile information retrieval is not included in this example
        Future.successful(Ok("Sign-in successful"))
      case Failure(ex) =>
        // Failed to retrieve access token
        Future.successful(BadRequest("Failed to sign in"))
  }
