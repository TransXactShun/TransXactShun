package com.example.transxactshun.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BillViewModel : ViewModel() {

    var reminderListLiveData = MutableLiveData<List<BillEntryWithId>>()

}