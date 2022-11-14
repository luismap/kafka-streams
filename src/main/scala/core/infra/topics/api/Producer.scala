package core.infra.topics.api

import org.apache.kafka.clients.producer.KafkaProducer

import java.util.Properties

trait Producer {
  def create[K,V](props: Properties): KafkaProducer[K,V]
}
