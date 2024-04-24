lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "ready",
    version := "1.0-SNAPSHOT",
    scalaVersion := "3.3.3",
    scalacOptions ++= Seq("-Werror"),
    scalafmtOnCompile := true,
    libraryDependencies ++= Seq(
      "com.google.apis" % "google-api-services-sheets" % "v4-rev612-1.25.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    )
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
