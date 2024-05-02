package controllers

import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleIdTokenVerifier}
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import models.Auth
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import java.util.Collections
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

// See https://developers.google.com/identity/gsi/web/guides/client-library
class AuthController(
    val controllerComponents: ControllerComponents,
    authCodeFlow: GoogleAuthorizationCodeFlow,
    idTokenVerifier: GoogleIdTokenVerifier,
    auth: Auth
) extends BaseController:

  def signIn(): Action[AnyContent] = Action(implicit request => Ok(views.html.signin(auth)))

  def signOut(): Action[AnyContent] = Action(implicit request => Ok(views.html.signout(auth)))

  val transport: HttpTransport = new NetHttpTransport()
  val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance
  val verifier: GoogleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
    // Specify the CLIENT_ID of the app that accesses the backend:
    .setAudience(Collections.singletonList(auth.clientId))
    // Or, if multiple clients access the backend:
    // .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
    .build()
  def signInRespond(): Action[AnyContent] = Action { implicit request =>
    val idToken = verifier.verify(request.body.asFormUrlEncoded.get("credential").head)
    if idToken != null then
      val payload = idToken.getPayload
      // Print user identifier
      val userId = payload.getSubject
      System.out.println("User ID: " + userId)
      // Get profile information from payload
      val email = payload.getEmail
      val emailVerified = payload.getEmailVerified
      val name = payload.get("name").asInstanceOf[String]
      val pictureUrl = payload.get("picture").asInstanceOf[String]
      val locale = payload.get("locale").asInstanceOf[String]
      val familyName = payload.get("family_name").asInstanceOf[String]
      val givenName = payload.get("given_name").asInstanceOf[String]
    else System.out.println("Invalid ID token.")
    Ok("details printed")
  }
