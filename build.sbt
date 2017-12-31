val Toml    = "0.1.1"
val Circe   = "0.8.0"
val ScalaJ  = "2.3.0"
val Commons = "1.11"

name := "crypto-portfolio"

version := "0.2.1-SNAPSHOT"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "io.circe"      %% "circe-core"    % Circe,
  "io.circe"      %% "circe-generic" % Circe,
  "io.circe"      %% "circe-parser"  % Circe,
  "org.scalaj"    %% "scalaj-http"   % ScalaJ,
  "tech.sparse"   %% "toml-scala"    % Toml,
  "commons-codec" %  "commons-codec" % Commons
)

assemblyOutputPath in assembly := file(".") / "portfolio.jar"
