package features.userpayment.domain.usecases

import features.userpayment.data.controllers.UserPaymentController
import features.userpayment.data.models.{OrderModel, PaymentModel}
import features.userpayment.domain.types.Types.{OrderId, ProductId, ProfileId, UserId}
import org.apache.kafka.streams.scala.kstream.KStream
import core.utils.KafkaSerializers.MysSerde
import features.userpayment.domain.entities.Order
import io.circe.generic.auto._
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._

import java.time.Duration
import java.time.temporal.ChronoUnit

object StreamHandlingShowdown {
  def apply(userPaymentController: UserPaymentController): StreamHandlingShowdown =
    new StreamHandlingShowdown(userPaymentController)
}

class StreamHandlingShowdown(
                              userPaymentController: UserPaymentController
                            ) {

  lazy val getExpensiveOrders: KStream[UserId, OrderModel] = {
    userPaymentController.getUserOrderStream.filter((userid, ordermodel) => {
      ordermodel.amount > 1000
    })
  }

  lazy val getPayments: KStream[OrderId, PaymentModel] = {
    userPaymentController.getPaymentsStream
  }

  lazy val getListOfProducts: KStream[UserId, List[ProductId]] = {
    userPaymentController.getUserOrderStream.mapValues(ordermodel => ordermodel.products)
  }

  lazy val ordersWithUserProfiles: KStream[UserId, (OrderModel, ProfileId)] = {
    val orders = userPaymentController.getUserOrderStream
    val profiles = userPaymentController.getDiscountProfileByUserTable
    orders.leftJoin(profiles) {
      case (order, profile) =>
        if (profile == null) (order, "") //using empty, because kafka streams, remove nulls
        else (order, profile)
    }
  }

  lazy val discountedOrdersStream: KStream[UserId, OrderModel] = {

    val discountC = (amount: Double, discount: Double) => amount - (amount * discount)

    val discountProfileGTable = userPaymentController.getDiscountProfileGlobalTable
    val discountedOrders = ordersWithUserProfiles.leftJoin(discountProfileGTable)(
      {
        case (userid, (order, profileId)) => profileId
      }, //key of the join, picked from left stream
      {
        case ((order, profile), discount) =>
          if (profile.isEmpty) order
          else order.copy(amount = discountC(order.amount, discount.amount))
      } //joiner, matched records
    )

    discountedOrders
  }

  lazy val ordersPaid = {
    val payments = userPaymentController.getPaymentsStream
    val discountedOrders = discountedOrdersStream
      .selectKey((userid, order) => order.orderId)

    //uses the times stamps of the records for matching window
    val joinWindow = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.of(30, ChronoUnit.SECONDS))

    val paidOrders = (order: OrderModel, payment: PaymentModel) =>
      if (payment == null) Option.empty[OrderModel]
      else if (payment.status == "PAID") Option(order)
      else Option.empty[OrderModel]


    discountedOrders.join(payments)(paidOrders, joinWindow)
      .flatMapValues(order => order.toList)

  }

}