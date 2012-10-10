import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mongoFile"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "net.vz.mongodb.jackson" % "mongo-jackson-mapper" % "1.4.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
