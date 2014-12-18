import Common._
import Dependencies._
import com.typesafe.sbt.web.SbtWeb
import play.PlayImport._
import play.PlayScala
import sbt.Keys._
import sbt._

name := """distributedTwitterCrawler"""

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .settings(appSettings: _*)
  .settings(uiSettings: _*)

libraryDependencies ++= Seq(
  ws,
  cache,
  gson,
  scalaTest,
  jodaTime,
  mockitoAll,
  postgresql,
  playTomcatCP,
  jquery,
  bootstrap,
  slick,
  akkaActor
)
