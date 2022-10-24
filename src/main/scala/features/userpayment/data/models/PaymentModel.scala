package features.userpayment.data.models

import features.userpayment.domain.entities.Payment
import features.userpayment.domain.types.Types.OrderId

case class PaymentModel(orderId: OrderId, status: String) extends Payment(orderId, status)
