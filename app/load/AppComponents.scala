package load

import com.google.api.client.googleapis.auth.oauth2.{
  GoogleAuthorizationCodeFlow,
  GoogleClientSecrets,
  GoogleIdTokenVerifier
}
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import controllers.*
import models.Auth
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes
import services.ReadingStorage
import services.impl.InMemReadingStorage

import java.io.FileReader
import java.util.Collections
import scala.jdk.CollectionConverters.*

abstract class AppComponentsBase(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AssetsComponents:

  val transport: HttpTransport = new NetHttpTransport()
  val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance

  def authCodeFlow: GoogleAuthorizationCodeFlow
  def idTokenVerifier: GoogleIdTokenVerifier

  private lazy val auth =
    Auth(clientId = configuration.get[String]("client.id"), loginUrl = configuration.get[String]("login.url"))

  lazy val readingStorage: ReadingStorage = new InMemReadingStorage()

  lazy val homeController = new HomeController(controllerComponents, transport, jsonFactory, authCodeFlow)
  lazy val authController = new AuthController(controllerComponents, authCodeFlow, idTokenVerifier, auth)
  lazy val readingController = new ReadingController(controllerComponents, readingStorage)

  lazy val router: Router =
    new Routes(httpErrorHandler, homeController, assets, readingController, authController)

class AppComponents(context: Context) extends AppComponentsBase(context):

  private val clientSecrets = GoogleClientSecrets.load(
    jsonFactory,
    new FileReader(configuration.get[String]("google.app.credentials"))
  )

  private val scopes = configuration.get[Seq[String]]("google.app.scopes").asJava

  override val authCodeFlow: GoogleAuthorizationCodeFlow =
    new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, scopes)
      .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance)
      .build()

  override val idTokenVerifier: GoogleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
    .setAudience(Collections.singletonList(configuration.get[String]("google.app.client.id")))
    .build();
