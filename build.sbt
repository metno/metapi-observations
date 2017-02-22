organization := "no.met.data"
name := """observations"""
version := "0.3-SNAPSHOT"
description := "Observations module of the metapi."
homepage :=  Some(url(s"https://github.com/metno"))
licenses += "GPL-2.0" -> url("https://www.gnu.org/licenses/gpl-2.0.html")

// Scala settings
// ----------------------------------------------------------------------
scalaVersion := "2.11.8"
scalacOptions ++= Seq("-deprecation", "-feature")
lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Play settings
// ----------------------------------------------------------------------
PlayKeys.devSettings += ("play.http.router", "observations.Routes")

// Dependencies
// ----------------------------------------------------------------------
libraryDependencies ++= Seq(
  jdbc,
  cache,
  evolutions,
  ws,
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",
  "io.swagger" %% "swagger-play2" % "1.5.2",
  "no.met.data" %% "util" % "0.3-SNAPSHOT",
  "no.met.data" %% "auth" % "0.3-SNAPSHOT",
  specs2 % Test
)

resolvers ++= Seq(
  "OJO Artifactory" at "http://oss.jfrog.org/artifactory/oss-snapshot-local",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

// Publish Settings
// ----------------------------------------------------------------------
publishTo := {
  val jfrog = "https://oss.jfrog.org/artifactory/"
  if (isSnapshot.value)
    Some("Artifactory Realm" at jfrog + "oss-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  else
    Some("Artifactory Realm" at jfrog + "oss-release-local")
}
pomExtra := (
  <scm>
    <url>https://github.com/metno/metapi-{name.value}.git</url>
    <connection>scm:git:git@github.com:metno/metapi-{name.value}.git</connection>
  </scm>
  <developers>
    <developer>
      <id>metno</id>
      <name>Meteorological Institute, Norway</name>
      <url>http://www.github.com/metno</url>
    </developer>
  </developers>)
bintrayReleaseOnPublish := false
publishArtifact in Test := false

// Testing
// ----------------------------------------------------------------------
coverageHighlighting := true
coverageMinimum := 95
coverageFailOnMinimum := true
coverageExcludedPackages := """
  <empty>;
  value.ApiResponse;
  ReverseApplication;
  ReverseAssets;
  observations.Routes;
  observations.RoutesPrefix;
"""

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true
