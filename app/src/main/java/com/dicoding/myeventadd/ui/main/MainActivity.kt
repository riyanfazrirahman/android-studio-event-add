package com.dicoding.myeventadd.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myeventadd.databinding.ActivityMainBinding
import com.dicoding.myeventadd.helper.ViewModelFactory
import com.dicoding.myeventadd.ui.insert.EventAddUpdateActivity

class MainActivity : AppCompatActivity() {
    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding

    private lateinit var adapter: EventAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val mainViewModel = obtainViewModel(this@MainActivity)
        mainViewModel.getAllEvent().observe(this) { eventList ->
            if (eventList != null) {
                adapter.setListEvents(eventList)
            }
        }

        adapter = EventAdapter()

        binding?.rvNotes?.layoutManager = LinearLayoutManager(this)
        binding?.rvNotes?.setHasFixedSize(true)
        binding?.rvNotes?.adapter = adapter

        binding?.fabAdd?.setOnClickListener {
            val intent = Intent(this@MainActivity, EventAddUpdateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }
}