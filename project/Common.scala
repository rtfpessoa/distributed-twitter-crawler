import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys
import com.typesafe.sbt.less.Import._
import com.typesafe.sbt.uglify.Import.{UglifyKeys, _}
import com.typesafe.sbt.web.Import._
import sbt.Keys._
import sbt._

object Common {

  val appSettings = Seq(
    version := "0.0.1",
    organization := "Unknown",
    scalaVersion := "2.11.3",
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-adapted-args", "-Xlint")
  )

  val uiSettings = Seq(
    JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,
    includeFilter in gzip := "*.html" || "*.css" || "*.js",
    DigestKeys.algorithms += "sha1",
    includeFilter in digest := "*.html" || "*.css" || "*.js",
    UglifyKeys.mangle := false,
    pipelineStages := Seq(uglify, digest, gzip),
    LessKeys.compress in Assets := true,
    includeFilter in(Assets, LessKeys.less) := "*.less",
    excludeFilter in(Assets, LessKeys.less) := "_*.less"
  )

}
