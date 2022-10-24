package features.userpayment.domain.entities

import features.userpayment.domain.types.Types.OrderId

abstract class Payment(
               orderId: OrderId,
               status: String
             )
