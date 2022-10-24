package features.userpayment.data.models

import features.userpayment.domain.entities.Discount
import features.userpayment.domain.types.Types.{ProfileId}

case class DiscountModel(profile: ProfileId, amount: Double) extends Discount (
  profile,
  amount
)