package features.userpayment.domain.controllers

import features.userpayment.data.models.{DiscountModel, OrderModel, PaymentModel}
import features.userpayment.domain.entities.{Order, Payment}
import features.userpayment.domain.types.Types.{OrderId, ProfileId, UserId}
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.{KafkaStreams, Topology}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}

trait UserPayment {
  def getUserOrderStream: KStream[UserId, OrderModel]

  /**
   * a keyed table is distributed
   *
   * @return
   */
  def getDiscountProfileByUserTable: KTable[UserId, ProfileId]

  /**
   * global table are copied to all nodes
   *
   * @return
   */
  def getDiscountProfileGlobalTable: GlobalKTable[ProfileId, DiscountModel]

  def getKafkaStream: KafkaStreams

  def getPaymentsStream: KStream[OrderId, PaymentModel]

  def getTopology: Topology
}
