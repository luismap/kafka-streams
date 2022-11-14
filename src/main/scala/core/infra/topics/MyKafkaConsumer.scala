package core.infra.topics

import core.infra.topics.api.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.Properties
import java.util.regex.Pattern

object MyKafkaConsumer extends Consumer {
  override def consumeFrom[K, V](topic: String, props: Properties): KafkaConsumer[K, V] = {
    val consumer = new KafkaConsumer[K,V](props)
    consumer.subscribe(Pattern.compile(topic))
    consumer
  }

}
