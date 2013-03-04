import AssemblyKeys._

name := "stats-o-tron"

version := System.getProperty("build.number", "DEV-BUILD")

scalaVersion := "2.9.1"

seq(assemblySettings: _*)

mainClass in assembly := Some("statsotron.StatsOTronApp")

resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net" at "http://download.java.net/maven/2/",
  "SNS Releases" at "http://repo.sns.sky.com:8081/artifactory/libs-releases/"
)

libraryDependencies ++= Seq(
  "org.noggin" % "noggin" % "118",
  "org.mongodb" %% "casbah" % "2.5.0",
  "se.scalablesolutions.akka" % "akka-actor" % "1.2",
  "org.scala-tools.time" %% "time" % "0.5",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default",
  "junit" % "junit" % "4.8" % "test->default",
  "org.mockito" % "mockito-all" % "1.9.5" % "test->default",
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test->default"
)
