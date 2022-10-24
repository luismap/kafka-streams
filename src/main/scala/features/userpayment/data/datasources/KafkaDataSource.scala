package features.userpayment.data.datasources

import org.apache.kafka.streams.{KafkaStreams, Topology}
import org.apache.kafka.streams.scala.StreamsBuilder

import java.util.Properties

class KafkaDataSource(
                       configs: Properties
                     ) {

  /**
   * given a builder, create the stream application
   * @param builder
   * @return
   */
  def getKafkaStream(builder: StreamsBuilder): KafkaStreams = {
    new KafkaStreams(builder.build(),configs)
  }
}