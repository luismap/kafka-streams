package core.utils

import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.Serde
import io.circe.syntax._
import io.circe.parser._
import org.apache.kafka.streams.scala.serialization.Serdes


object KafkaSerializers {
  /**
   * given a model, serialize to kafka specific
   * using context bounds for implicits
   * @tparam A
   * @return
   */
  implicit def MysSerde[A >: Null : Decoder : Encoder]: Serde[A] = {
    val serializer = (a: A) => a.asJson.noSpaces.getBytes()
    val deserializer = (bytes: Array[Byte] ) => {
      val string = new String(bytes)
      decode[A](string).toOption
    }

    Serdes.fromFn[A](serializer, deserializer)
  }
}
