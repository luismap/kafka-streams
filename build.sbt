ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / resolvers += "confluent" at "https://packages.confluent.io/maven"

val kafkaVersion = "3.2.1"
val circeVersion = "0.14.2"

lazy val root = (project in file("."))
  .settings(
    name := "Kafka",
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % kafkaVersion,
      "org.apache.kafka" % "kafka-streams" % kafkaVersion,
      "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "log4j" % "log4j" % "1.2.17",
      "org.apache.avro" % "avro" % "1.11.0",
      "io.confluent" % "kafka-avro-serializer" % "7.0.0"
    )
  )
