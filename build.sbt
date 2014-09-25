name := """netdict"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalikejdbc" % "scalikejdbc_2.11" % "2.1.2",
  "org.scalikejdbc" % "scalikejdbc-play-plugin_2.11" % "2.3.2",
  "ch.qos.logback"  %  "logback-classic" % "1.1.2",
  "org.scalatestplus" % "play_2.11" % "1.2.0",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41"
)

scalikejdbcSettings
