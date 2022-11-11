package features.userpayment.presentation

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import java.util.Properties
import scala.io.Source

object SimpleProducer extends App {

  val props = new Properties()
  props.load(this.getClass.getResourceAsStream("/secret_kafka_cluster.properties"))
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("group.id", "simple_producer01")

  val producer = new KafkaProducer[String, String](props)

  while (true) {
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
