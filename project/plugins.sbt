resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

//Test reporter
addSbtPlugin("com.gu" % "sbt-teamcity-test-reporting-plugin" % "1.5")

//web
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

//plugin updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.6")
