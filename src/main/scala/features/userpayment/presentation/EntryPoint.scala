package features.userpayment.presentation

import features.userpayment.data.controllers.UserPaymentController
import features.userpayment.data.datasources.KafkaDataSource
import features.userpayment.domain.usecases.{CheckStreams, StreamHandlingShowdown}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.log4j.Logger

import java.util.{Properties, UUID}

object EntryPoint extends App {


  val props = new Properties
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, UUID.randomUUID().toString)
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass)
  props.put("auto.offset.reset", "earliest")
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")


  val logger = Logger.getLogger(this.getClass.getName)

  val builder = new StreamsBuilder
  val kafkaDataSource = new KafkaDataSource(props)
  val userPaymentController = new UserPaymentController(kafkaDataSource, builder)

  val checkStreams = new CheckStreams(userPaymentController)
  val streamHandlingShowdown = new StreamHandlingShowdown(userPaymentController)


  val expensiveOrdersStream = streamHandlingShowdown.getExpensiveOrders
  val listOfProducts = streamHandlingShowdown.getListOfProducts
  val ordersWithProfiles = streamHandlingShowdown.ordersWithUserProfiles
  val discountedOrders = streamHandlingShowdown.discountedOrdersStream
  val payments = streamHandlingShowdown.getPayments
  val paidOrders = streamHandlingShowdown.ordersPaid

  def logStreams = {
    checkStreams.checkOrders.peek((k, v) => logger.info(s"[orders-by-user] $k data: $v"))
    expensiveOrdersStream.peek((k, v) => logger.info(s"[expensive-orders] $k data: $v"))
    listOfProducts.peek((k, v) => logger.info(s"[list-of-products] $k data: $v"))
    ordersWithProfiles.peek((k, v) => logger.info(s"[orders-with-profiles] $k data: $v"))
    discountedOrders.peek((k, v) => logger.info(s"[discounted-orders] $k data: $v"))
    payments.peek((k,v) => logger.info(s"[payments] $k data: $v"))
    paidOrders.peek((k,v) => logger.info(s"[paid] $k data: $v"))

  }


  logStreams
  import core.utils.KafkaSerializers.MysSerde
  import io.circe.generic.auto._
  import org.apache.kafka.streams.scala.ImplicitConversions._
  paidOrders.to("paid-orders")

  val topology = userPaymentController.getTopology
  logger.info(topology.describe())
  val app = userPaymentController.getKafkaStream
  app.start()

  //app.close(Duration.ofSeconds(6))
}
