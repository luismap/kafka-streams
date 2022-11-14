package core.infra.topics.api

import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.Properties

trait Consumer {

  def consumeFrom[K, V](topic: String, props: Properties): KafkaConsumer[K, V]

}
