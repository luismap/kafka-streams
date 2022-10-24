package features.userpayment.domain.entities

import features.userpayment.domain.types.Types.ProfileId

abstract class Discount(
                profile: ProfileId,
                amount: Double
              )
