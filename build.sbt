ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "dissections",
    idePackagePrefix := Some("dev.robertjohnnorman.dissections"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-collections-core" % "0.9.8",
      "org.typelevel" %% "kittens" % "3.3.0",
      "org.typelevel" %% "mouse" % "1.3.2",
      "io.higherkindness" %% "droste-core" % "0.9.0",
      "io.higherkindness" %% "droste-meta" % "0.9.0",
      "io.higherkindness" %% "droste-macros" % "0.9.0",
      "org.typelevel" %% "shapeless3-deriving" % "3.4.0",
      "org.typelevel" %% "shapeless3-typeable" % "3.4.0",
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.typelevel" %% "cats-effect-testkit" % "3.5.4" % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test
    ),
    scalacOptions += "log-implicits"
  )
