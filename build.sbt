import Dependencies._

name := """play-extensions"""
organization := "kipsigman"

scalaVersion := "2.11.7"

resolvers += "Kaliber Internal Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"
"net.kaliber" %% "play-s3" % "7.0.2"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "kipsigman" %% "scala-domain-model" % "0.1.5",
  "net.kaliber" %% "play-s3" % "7.0.2",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % Test
)

licenses += ("Apache-2.0", url("https://github.com/kipsigman/play-auth/blob/master/LICENSE"))
homepage := Some(url("https://github.com/kipsigman/play-extensions"))
scmInfo := Some(ScmInfo(url("https://github.com/kipsigman/play-extensions"), "scm:git:git://github.com:kipsigman/play-extensions.git"))
