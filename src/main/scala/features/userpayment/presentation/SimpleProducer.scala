package features.userpayment.presentation

import core.infra.topics.MyKafkaProducer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.log4j.Logger

import java.util.Properties
import scala.io.Source

object SimpleProducer extends App {

  val logger = Logger.getLogger(this.getClass.getName)
  val props = new Properties()
  props.load(this.getClass.getResourceAsStream("/secret_kafka_cluster.properties"))
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("group.id", "simple_producer01")

  val producer =  MyKafkaProducer.create[String, String](props)
  var keepRunning = true
  val mainThread = Thread.currentThread()

  Runtime.getRuntime().addShutdownHook(new Thread(() => {
    keepRunning = false
    logger.info("got into new thread")
    mainThread.join()
  }))

  while (keepRunning) {
      val content = Source.stdin.getLines()
      for (
        line <- content
      ) {
        val data = line.split(":")
        producer.send(new ProducerRecord[String, String]("poems", data(0), data(1)))
      }
      producer.commitTransaction()

  }

}
