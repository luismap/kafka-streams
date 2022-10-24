package features.userpayment.domain.entities

import features.userpayment.domain.types.Types.{OrderId, UserId, ProductId}

abstract class Order(
             orderId: OrderId,
             user: UserId,
             products: List[ProductId],
             amount: Double
           )

