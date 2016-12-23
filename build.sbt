import Dependencies._

name := """play-extensions"""
organization := "kipsigman"

scalaVersion := "2.11.8"

resolvers += Resolver.bintrayRepo("kipsigman", "maven")
resolvers += "Kaliber Internal Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % playVersion,
  "kipsigman" %% "scala-domain-model" % "0.3.3",
  "commons-io" % "commons-io" % "2.5",
  "net.kaliber" %% "play-s3" % "8.0.0",
  "org.jsoup" % "jsoup" % "1.10.1",
  "org.jdom" % "jdom2" % "2.0.6",
  "org.mockito" % "mockito-core" % "2.3.11" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

licenses += ("Apache-2.0", url("https://github.com/kipsigman/play-auth/blob/master/LICENSE"))
homepage := Some(url("https://github.com/kipsigman/play-extensions"))
scmInfo := Some(ScmInfo(url("https://github.com/kipsigman/play-extensions"), "scm:git:git://github.com:kipsigman/play-extensions.git"))
