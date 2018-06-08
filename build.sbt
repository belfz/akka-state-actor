name := "akka-nowa"

version := "0.1"

scalaVersion := "2.12.6"

val akka = Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.12"
)

libraryDependencies ++= akka ++ Seq("org.scalatest" %% "scalatest" % "3.0.5" % Test)
