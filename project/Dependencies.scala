import sbt._

object Dependencies {

  // Generic
  lazy val gson           = "com.google.code.gson"      % "gson"                         % "2.3.1"
  lazy val jodaTime       = "joda-time"                 % "joda-time"                    % "2.5"
  lazy val postgresql     = "org.postgresql"            % "postgresql"                   % "9.3-1102-jdbc41"

  // Codacy
  lazy val playTomcatCP    = "com.codacy"              %% "play-tomcatcp"                % "1.0.1"

  // Test-Only
  lazy val scalaTest      = "org.scalatest"            %% "scalatest"                    % "2.2.2" % "test"
  lazy val mockitoAll     = "org.mockito"               % "mockito-all"                  % "1.10.8" % "test"

  // Typesafe
  lazy val slick          = "com.typesafe.slick"       %% "slick"                        % "2.1.0"
  lazy val akkaActor      = "com.typesafe.akka"        %% "akka-actor"                   % "2.3.6"

  //WebJars
  lazy val jquery         = "org.webjars"               % "jquery"                       % "2.1.1"

}
