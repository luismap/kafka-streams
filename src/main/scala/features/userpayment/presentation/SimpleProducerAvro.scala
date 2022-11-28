package features.userpayment.presentation

import core.infra.topics.MyKafkaProducer
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.log4j.Logger
import org.apache.kafka.common.serialization.StringSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializer

import java.time.Duration
import java.util.Properties
import scala.io.Source

object SimpleProducerAvro extends App {


  val logger = Logger.getLogger(this.getClass.getName)
  val props = new Properties()

  props.load(this.getClass.getResourceAsStream("/secret_kafka_cluster.properties"))
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  //props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("group.id", "simple_producer05")

  val ins = this.getClass.getResourceAsStream("/schemas/schema-avroTest-value-v1.avsc")
  val schema = new Schema.Parser().parse(ins)
  ins.close()
  val record = new GenericData.Record(schema)
  logger.info(schema.toString(true))

  val producer = new KafkaProducer[String, GenericData.Record](props)

  record.put("artistId", "luis")
  record.put("content", "234.23")


  logger.info(s"produced: $record")

  while (true) {
    var data = Source.stdin.getLines()
    for (
      line <- data
    ){
      val row = line.split(":")
      record.put("artistId", row(1))
      record.put("content", row(2))
      try {
        val ack = producer
          .send(new ProducerRecord[String, GenericData.Record]("correctAvro", row(0), record))
          .get()

        producer.flush()
        // grabbing the ack and logging for visibility
        logger.info(ack)
        logger.info(s"produced $record")
      }
      catch {
        case e: Throwable => logger.error(e.getMessage, e)
      }
    }

  }
}
