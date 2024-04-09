package load

import controllers.{AssetsComponents, HomeController}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import router.Routes
import play.filters.HttpFiltersComponents

class AppComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AssetsComponents {
  lazy val homeController: HomeController = new HomeController(controllerComponents)
  override lazy val router: Router = new Routes(httpErrorHandler, homeController, assets)
}
