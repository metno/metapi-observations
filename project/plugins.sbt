// BinTray
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

// Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.8")

// Eclipse support
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

// Test plugins
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

// Scalastyle Plugin
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")

resolvers ++= Seq(
  Resolver.jcenterRepo
)
