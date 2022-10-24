package features.userpayment.data.controllers

import features.userpayment.data.datasources.KafkaDataSource
import features.userpayment.data.models.{DiscountModel, OrderModel, PaymentModel}
import features.userpayment.domain.controllers.UserPayment
import features.userpayment.domain.entities.Payment
import features.userpayment.domain.types.Types.{OrderId, ProfileId, UserId}
import io.circe.generic.auto._
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.apache.kafka.streams.scala.ImplicitConversions.consumedFromSerde
import org.apache.kafka.streams.{KafkaStreams, Topology}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}

class UserPaymentController(kafkaDataSource: KafkaDataSource,
                            builder: StreamsBuilder) extends UserPayment {


  import core.utils.KafkaSerializers._

  /**
   * global table are copied to all nodes
   *
   * @return
   */
  override def getDiscountProfileGlobalTable: GlobalKTable[ProfileId, DiscountModel] = {
    builder.globalTable[ProfileId, DiscountModel]("discounts")
  }

  override def getUserOrderStream: KStream[UserId, OrderModel] = {
    builder.stream[UserId, OrderModel]("orders-by-user")
  }

  override def getDiscountProfileByUserTable: KTable[UserId, ProfileId] = {
    builder.table[UserId, ProfileId]("discount-profiles-by-user")
  }

  def getKafkaStream: KafkaStreams = {
    kafkaDataSource.getKafkaStream(builder)
  }

  override def getPaymentsStream: KStream[OrderId, PaymentModel] = {
    builder.stream[OrderId, PaymentModel]("payments")
  }

  override def getTopology: Topology = builder.build()
}
