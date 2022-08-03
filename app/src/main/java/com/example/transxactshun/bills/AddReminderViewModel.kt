package com.example.transxactshun.bills

import androidx.lifecycle.ViewModel
import java.util.*

class AddReminderViewModel: ViewModel() {


    // values to store data to be stored to db
    var payeeName: String? = ""
    var remindOn: Calendar? = null
    var amount: Double = 0.0
    var memo: String? = ""


}