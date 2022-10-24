package features.userpayment.domain.usecases

import features.userpayment.data.controllers.UserPaymentController

class CheckStreams(
  userPaymentController: UserPaymentController
) {
  def checkDiscounts = {
    userPaymentController.getDiscountProfileGlobalTable
  }

  def checkOrders = {
    userPaymentController.getUserOrderStream
  }

  def checkDiscountProfileByUserTable = {
    userPaymentController.getDiscountProfileByUserTable
  }
}
