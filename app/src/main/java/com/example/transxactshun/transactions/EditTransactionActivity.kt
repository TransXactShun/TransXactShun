package com.example.transxactshun.transactions

import android.R.attr.button
import android.R.attr.min
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.R
import com.example.transxactshun.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLOutput
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class EditTransactionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    // EditText fields
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var spinnerPaymentType: Spinner
    private lateinit var spinnerCategoryType: Spinner

    //    private lateinit var purchaseLocationEditText: EditText
    private lateinit var purchaseNotesEditText: EditText
    private lateinit var purchaseDateEditText: TextView
    private lateinit var purchaseTimeEditText: TextView
    private lateinit var purchaseDateImageView: ImageView
    private lateinit var purchaseTimeImageView: ImageView

    // Buttons
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button

    private lateinit var transactionSelected: ExpensesDatabaseEntry
    private lateinit var editTransactionDatabase: ExpensesDatabase
    private lateinit var editTransactionDao: ExpensesDatabaseDao
    private lateinit var editTransactionRepository: ExpensesRepository
    private lateinit var editTransactionViewModelFactory: EditTransactionViewModelFactory
    lateinit var editTransactionViewModel: EditTransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        // Initialize Buttons
        saveButton = findViewById(R.id.saveTransactionEdit)
        cancelButton = findViewById(R.id.cancelTransactionEdit)
        deleteButton = findViewById(R.id.deleteTransaction)

        // Initialize EditText fields
        itemNameEditText = findViewById(R.id.editItemName)
        itemPriceEditText = findViewById(R.id.editItemPrice)
        spinnerPaymentType = findViewById(R.id.spinnerPaymentMethod)
        spinnerCategoryType = findViewById(R.id.spinnerItemCategoryMethod)
//        purchaseLocationEditText = findViewById(R.id.editPurchaseLocation)
        purchaseNotesEditText = findViewById(R.id.editPurchaseNotes)
        purchaseDateEditText = findViewById(R.id.editPurchaseDate)
        purchaseTimeEditText = findViewById(R.id.editPurchaseTime)

        // add data to spinner
        spinnerPaymentType.adapter = ArrayAdapter<PaymentType>(
            this,
            android.R.layout.simple_list_item_1,
            PaymentType.values()
        )
        spinnerCategoryType.adapter = ArrayAdapter<ExpenseCategory>(
            this,
            android.R.layout.simple_list_item_1,
            ExpenseCategory.values()
        )

        CoroutineScope(Dispatchers.IO).launch {
            editTransactionDatabase = ExpensesDatabase.getInstance(this@EditTransactionActivity)
            editTransactionDao = editTransactionDatabase.expensesDatabaseDao
            editTransactionRepository = ExpensesRepository(editTransactionDao)
            editTransactionViewModelFactory =
                EditTransactionViewModelFactory(editTransactionRepository)
            editTransactionViewModel =
                ViewModelProvider(
                    this@EditTransactionActivity,
                    editTransactionViewModelFactory
                )[EditTransactionViewModel::class.java]
            withContext(Main) {
                editTransactionViewModel.id.value =
                    intent.getLongExtra(TransactionHistoryActivity.TRANSACTION_ID_KEY, -1)
                editTransactionViewModel.position.value =
                    intent.getIntExtra(TransactionHistoryActivity.TRANSACTION_POSITION_KEY, -1)
                editTransactionViewModel.items.value =
                    intent.getStringExtra(TransactionHistoryActivity.TRANSACTION_NAME_KEY)
                editTransactionViewModel.cost.value =
                    intent.getIntExtra(TransactionHistoryActivity.TRANSACTION_PRICE_KEY, 0)
                editTransactionViewModel.paymentType.value =
                    intent.getIntExtra(TransactionHistoryActivity.TRANSACTION_PAYMENT_KEY, 0)
                editTransactionViewModel.category.value = intent.getIntExtra(
                    TransactionHistoryActivity.TRANSACTION_CATEGORY_KEY,
                    ExpenseCategory.OTHER.ordinal
                )
//        val purchaseLocation = intent.getStringExtra("purchaseLocation")
                editTransactionViewModel.note.value =
                    intent.getStringExtra(TransactionHistoryActivity.TRANSACTION_NOTES_KEY)
                editTransactionViewModel.epochDate.value =
                    intent.getLongExtra(TransactionHistoryActivity.TRANSACTION_DATE_KEY, 0)
                editTransactionViewModel.email.value =
                    intent.getStringExtra(TransactionHistoryActivity.TRANSACTION_EMAIL_KEY)
                editTransactionViewModel.vendor.value =
                    intent.getStringExtra(TransactionHistoryActivity.TRANSACTION_VENDOR_KEY)
                itemNameEditText.setText(editTransactionViewModel.items.value)
                itemPriceEditText.setText(
                    TransactionUtil.currencyFormatWithoutSign(
                        editTransactionViewModel.cost.value!!
                    )
                )
//        purchaseLocationEditText.setText(purchaseLocation)
                purchaseNotesEditText.setText(editTransactionViewModel.note.value)
                purchaseDateEditText.text =
                    TransactionUtil.millisecondsToDateTimeFormat(editTransactionViewModel.epochDate.value!!)
                spinnerPaymentType.setSelection(editTransactionViewModel.paymentType.value!!)
                spinnerCategoryType.setSelection(editTransactionViewModel.category.value!!)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = editTransactionViewModel.epochDate.value!!
                val yy = calendar.get(Calendar.YEAR)
                val mm = calendar.get(Calendar.MONTH)
                val dd = calendar.get(Calendar.DAY_OF_MONTH)
                val hh = calendar.get(Calendar.HOUR_OF_DAY)
                val min = calendar.get(Calendar.MINUTE)
                editTransactionViewModel.year.value = yy
                editTransactionViewModel.month.value = mm
                editTransactionViewModel.day.value = dd
                editTransactionViewModel.hour.value = hh
                editTransactionViewModel.minute.value = min
                purchaseDateEditText.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(
                        this@EditTransactionActivity,
                        this@EditTransactionActivity,
                        yy,
                        mm,
                        dd
                    )
                    datePickerDialog.show()
                }
                purchaseDateImageView.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(this@EditTransactionActivity, this@EditTransactionActivity, yy, mm, dd)
                    datePickerDialog.show()
                }
                purchaseTimeEditText.setOnClickListener {
                    val timePickerDialog = TimePickerDialog(
                        this@EditTransactionActivity,
                        this@EditTransactionActivity,
                        hh,
                        min,
                        false
                    )
                    timePickerDialog.show()
                }
                purchaseTimeImageView.setOnClickListener {
                    val timePickerDialog = TimePickerDialog(this@EditTransactionActivity, this@EditTransactionActivity, hh, min, false)
                    timePickerDialog.show()
                }

                editTransactionViewModel.epochDate.observe(this@EditTransactionActivity) {
                    purchaseDateEditText.text =
                        TransactionUtil.millisecondsToDateFormat(editTransactionViewModel.epochDate.value!!)
                    purchaseTimeEditText.text =
                        TransactionUtil.millisecondsToTimeFormat(editTransactionViewModel.epochDate.value!!)
                }
            }
        }

        saveButton.setOnClickListener() {
            // Obtain current EditText values
            val itemNameEdit: String = itemNameEditText.text.toString()
            println("TRACE: CHECKING NAME STRING IN EDIT TEXT")
            println(itemNameEdit)
            val itemPriceEdit: Int = (itemPriceEditText.text.toString().replace(".", "")).toInt()
            val paymentMethodEdit: Int = spinnerPaymentType.selectedItemPosition
            val purchaseNotesEdit: String = purchaseNotesEditText.text.toString()
            val itemCategoryEdit: Int = spinnerCategoryType.selectedItemPosition
            // Since email and vendor cannot be adjusted, do not include them in the entry
            val newEntry = ExpensesDatabaseEntry(
                editTransactionViewModel.id.value!!,
                "",
                itemPriceEdit,
                itemNameEdit,
                editTransactionViewModel.epochDate.value!!,
                paymentMethodEdit,
                purchaseNotesEdit,
                itemCategoryEdit,
                ""
            )
            editTransactionViewModel.adjustEntry(newEntry)


            this.finish()
        }

        cancelButton.setOnClickListener() {
            // Exit activity
            this.finish()
        }

        deleteButton.setOnClickListener() {
            try {
                editTransactionViewModel.deleteEntry(editTransactionViewModel.id.value!!)
                this.finish()
            } catch (ex: Exception) {
                Toast.makeText(
                    baseContext,
                    "Unable to delete entry!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        editTransactionViewModel.year.value = year
        editTransactionViewModel.month.value = month + 1
        editTransactionViewModel.day.value = dayOfMonth
        val hour = editTransactionViewModel.hour.value!!
        val minute = editTransactionViewModel.minute.value!!
        val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val dayStr = if (dayOfMonth < 10) "0${dayOfMonth}" else "$dayOfMonth"
        val hourStr = if (hour < 10) "0${hour}" else "$hour"
        val minuteStr = if (minute < 10) "0$minute}" else "$minute"
        val dateString = "${year}-${monthStr}-${dayStr}T${hourStr}:${minuteStr}:00"
        editTransactionViewModel.epochDate.value = LocalDateTime.parse(dateString).toInstant(
            ZoneOffset.UTC
        ).toEpochMilli()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        editTransactionViewModel.hour.value = hourOfDay
        editTransactionViewModel.minute.value = minute
        val month = editTransactionViewModel.month.value!!
        val day = editTransactionViewModel.day.value!!
        val monthStr = if (month < 10) "0${month}" else "$month"
        val dayStr = if (day < 10) "0${day}" else "$day"
        val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "$hourOfDay"
        val minuteStr = if (minute < 10) "0${minute}" else "$minute"
        val dateString =
            "${editTransactionViewModel.year.value}-${monthStr}-${dayStr}T${hourStr}:${minuteStr}:00"
        editTransactionViewModel.epochDate.value = LocalDateTime.parse(dateString).toInstant(
            ZoneOffset.UTC
        ).toEpochMilli()
    }
}