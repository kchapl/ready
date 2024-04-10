package load

import controllers.{AssetsComponents, HomeController, ReadingController}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import router.Routes
import play.filters.HttpFiltersComponents
import services.ReadingStorage
import services.impl.InMemReadingStorage

class AppComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AssetsComponents:
  lazy val readingStorage: ReadingStorage = new InMemReadingStorage()
  lazy val homeController: HomeController = new HomeController(controllerComponents)
  lazy val readingController: ReadingController = new ReadingController(controllerComponents, readingStorage)
  override lazy val router: Router = new Routes(httpErrorHandler, homeController, assets, readingController)
