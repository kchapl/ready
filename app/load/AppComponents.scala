package load

import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import controllers.*
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes
import services.ReadingStorage
import services.impl.InMemReadingStorage

import java.io.FileReader
import scala.jdk.CollectionConverters.*

abstract class AppComponentsBase(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AssetsComponents:

  val transport: HttpTransport = new NetHttpTransport()
  val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance

  def authCodeFlow: GoogleAuthorizationCodeFlow

  lazy val readingStorage: ReadingStorage = new InMemReadingStorage()

  lazy val homeController = new HomeController(controllerComponents, transport, jsonFactory, authCodeFlow)
  lazy val authController = new AuthController(controllerComponents, authCodeFlow)
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
