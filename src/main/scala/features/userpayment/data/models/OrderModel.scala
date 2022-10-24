package features.userpayment.data.models

import features.userpayment.domain.entities.Order
import features.userpayment.domain.types.Types.{OrderId, ProductId, UserId}

case class OrderModel(
                       orderId: OrderId,
                       user: UserId,
                       products: List[ProductId],
                       amount: Double
                     )
  extends Order(orderId, user, products, amount)
