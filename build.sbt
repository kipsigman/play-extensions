import Dependencies._

name := """play-extensions"""
organization := "kipsigman"

scalaVersion := "2.11.7"

resolvers += Resolver.bintrayRepo("kipsigman", "maven")
resolvers += "Kaliber Internal Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"

libraryDependencies ++= Seq(
  "kipsigman" %% "scala-domain-model" % "0.2.1",
  "commons-io" % "commons-io" % "2.4",
  "net.kaliber" %% "play-s3" % "7.0.2",
  "org.julienrf" %% "play-jsmessages" % "2.0.0",
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.jdom" % "jdom2" % "2.0.6",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "com.typesafe.play" %% "play-test" % playVersion % Test
)

licenses += ("Apache-2.0", url("https://github.com/kipsigman/play-auth/blob/master/LICENSE"))
homepage := Some(url("https://github.com/kipsigman/play-extensions"))
scmInfo := Some(ScmInfo(url("https://github.com/kipsigman/play-extensions"), "scm:git:git://github.com:kipsigman/play-extensions.git"))
