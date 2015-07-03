name := """observations"""

organization := "no.met.data"

version := "0.2-SNAPSHOT"

publishTo := {
  val nexus = "http://maven.met.no/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

PlayKeys.devSettings += ("play.http.router", "observations.Routes")


// Test Settings
javaOptions += "-Djunit.outdir=target/test-report"

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := true

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 95

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := true

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := """
  <empty>;
  value.ApiResponse;
  ReverseApplication;
  ReverseAssets;
  observations.Routes;
  views.html;
"""


// Dependencies
libraryDependencies ++= Seq(
  jdbc,
  cache,
  evolutions,
  ws,
 "com.typesafe.play" %% "anorm" % "2.4.0",
 "pl.matisoft" %% "swagger-play24" % "1.4",
 "com.github.nscala-time" %% "nscala-time" % "2.0.0",
 "com.oracle" % "ojdbc14" % "10.2.0.1.0",
 "no.met.data" %% "util" % "0.2-SNAPSHOT",
 "no.met.data" %% "auth" % "0.2-SNAPSHOT",
  specs2 % Test
)

resolvers ++= Seq( "metno repo" at "http://maven.met.no/content/groups/public",
                   "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases" )

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
