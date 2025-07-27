import de.heikoseeberger.sbtheader.License

name := """data-accessor-redis-web"""
organization := "com.ideal.linked"
version := "0.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(AutomateHeaderPlugin)

scalaVersion := "2.13.11"

libraryDependencies += guice
libraryDependencies += "com.ideal.linked" %% "scala-common" % "0.6"
libraryDependencies += "com.ideal.linked" %% "toposoid-common" % "0.6"
libraryDependencies += "com.ideal.linked" %% "toposoid-deduction-protocol-model" % "0.6"
libraryDependencies += "io.lettuce" % "lettuce-core" % "6.3.2.RELEASE"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

organizationName := "Linked Ideal LLC.[https://linked-ideal.com/]"
startYear := Some(2021)
licenses += ("AGPL-3.0-or-later", new URL("http://www.gnu.org/licenses/agpl-3.0.en.html"))
headerLicense := Some(License.AGPLv3("2025", organizationName.value))
