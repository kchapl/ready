package models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class Auth(clientId: String, loginUrl: String)

object Auth:
  implicit val configLoader: ConfigLoader[Auth] = (rootConfig: Config, path: String) =>
    val config = rootConfig.getConfig(path)
    Auth(
      clientId = config.getString("client_id"),
      loginUrl = "localhost:9000/abc"
    )
