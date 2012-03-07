import AssemblyKeys._

name := "stats-o-tron"

scalaVersion := "2.9.1"

seq(assemblySettings: _*)

mainClass in assembly := Some("sky.sns.statsotron.StatsOTronApp")

resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net" at "http://download.java.net/maven/2/",
  "SNS Releases" at "http://repo.sns.sky.com:8081/artifactory/libs-releases/"
)

libraryDependencies ++= Seq(
  "org.noggin" % "noggin" % "32",
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
  "se.scalablesolutions.akka" % "akka-actor" % "1.2",
  "org.scala-tools.time" %% "time" % "0.5",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default",
  "junit" % "junit" % "4.8" % "test->default",
  "org.mockito" % "mockito-all" % "1.8.5" % "test->default",
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test->default"
)