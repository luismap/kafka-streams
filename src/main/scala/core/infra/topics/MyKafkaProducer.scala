package core.infra.topics

import core.infra.topics.api.Producer
import org.apache.kafka.clients.producer.KafkaProducer

import java.util.Properties

object MyKafkaProducer extends Producer {
  override def create[K, V](props: Properties): KafkaProducer[K, V] =
    new KafkaProducer[K,V](props)
}
