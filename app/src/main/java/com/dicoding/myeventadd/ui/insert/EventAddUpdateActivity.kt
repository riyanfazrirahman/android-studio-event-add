package com.dicoding.myeventadd.ui.insert

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.myeventadd.R
import com.dicoding.myeventadd.database.Event
import com.dicoding.myeventadd.databinding.ActivityEventAddUpdateBinding
import com.dicoding.myeventadd.helper.DateHelper
import com.dicoding.myeventadd.helper.ViewModelFactory

class EventAddUpdateActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_EVENT = "extra_event"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
    private var isEdit = false
    private var event: Event? = null

    private lateinit var eventAddUpdateViewModel: EventAddUpdateViewModel
    private var _activityEventAddUpdateBinding: ActivityEventAddUpdateBinding? = null
    private val binding get() = _activityEventAddUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityEventAddUpdateBinding = ActivityEventAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        eventAddUpdateViewModel = obtainViewModel(this@EventAddUpdateActivity)

        event = intent.getParcelableExtra(EXTRA_EVENT)
        if (event != null) {
            isEdit = true
        } else {
            event = Event()
        }
        val actionBarTitle: String
        val btnTitle: String
        if (isEdit) {
            actionBarTitle = getString(R.string.change)
            btnTitle = getString(R.string.update)
            if (event != null) {
                event?.let { event ->
                    binding?.edtTitle?.setText(event.title)
                    binding?.edtDescription?.setText(event.description)
                }
            }
        } else {
            actionBarTitle = getString(R.string.add)
            btnTitle = getString(R.string.save)
        }
        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.btnSubmit?.text = btnTitle

        binding?.btnSubmit?.setOnClickListener {
            val title = binding?.edtTitle?.text.toString().trim()
            val description = binding?.edtDescription?.text.toString().trim()
            when {
                title.isEmpty() -> {
                    binding?.edtTitle?.error = getString(R.string.empty)
                }
                description.isEmpty() -> {
                    binding?.edtDescription?.error = getString(R.string.empty)
                }
                else -> {
                    event.let { event ->
                        event?.title = title
                        event?.description = description
                    }
                    if (isEdit) {
                        eventAddUpdateViewModel.update(event as Event)
                        showToast(getString(R.string.changed))
                    } else {
                        event.let { event ->
                            event?.date = DateHelper.getCurrentDate()
                        }
                        eventAddUpdateViewModel.insert(event as Event)
                        showToast(getString(R.string.added))
                    }
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog(ALERT_DIALOG_CLOSE)
            }
        })

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String
        if (isDialogClose) {
            dialogTitle = getString(R.string.cancel)
            dialogMessage = getString(R.string.message_cancel)
        } else {
            dialogMessage = getString(R.string.message_delete)
            dialogTitle = getString(R.string.delete)
        }
        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!isDialogClose) {
                    eventAddUpdateViewModel.delete(event as Event)
                    showToast(getString(R.string.deleted))
                }
                finish()
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityEventAddUpdateBinding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): EventAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(EventAddUpdateViewModel::class.java)
    }
}