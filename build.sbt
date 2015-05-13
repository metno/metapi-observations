name := """metapi-observations"""

organization := "no.met.data"

version := "0.1-SNAPSHOT"

publishTo := {
  val nexus = "http://maven.met.no/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

scalacOptions += "-feature"

javaOptions += "-Djunit.outdir=target/test-report"

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := true

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 80

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := true

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := """
  <empty>;
  util.HttpStatus;views.html.swaggerUi.*;
  value.ApiResponse;
  ReverseApplication;
  ReverseAssets;
  Routes;
"""

scalacOptions += "-feature"

scalacOptions += "-language:postfixOps"

resolvers += "metno repo" at "http://maven.met.no/content/groups/public"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
 "com.wordnik" %% "swagger-play2" % "1.3.12",
 "com.wordnik" %% "swagger-play2-utils" % "1.3.12",
 "com.github.nscala-time" %% "nscala-time" % "1.8.0",
 "com.oracle" % "ojdbc14" % "10.2.0.1.0",
  ws,
  "no.met.data" %% "metapi-auth" % "0.1-SNAPSHOT"
)

PlayKeys.devSettings += ("application.router", "observations.Routes")
