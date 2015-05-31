name := """my-first-app"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1"
)

fork in run := false

javaOptions ++= Seq("-Xmx2048M", "-Xmx2048M", "-XX:MaxPermSize=2048M")