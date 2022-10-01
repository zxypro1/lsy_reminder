package com.example.lsy_reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    private var pi: PendingIntent? = null
    private var alarmManager: AlarmManager? = null
    private var sleepButton: Button? = null
    private lateinit var tt : TimeTask<TimeTask.Task>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sleepButton = findViewById<Button>(R.id.sleep_button)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        sleepButton?.setOnClickListener {
            Toast.makeText(this, "成功点击", Toast.LENGTH_SHORT).show()
        }
    }
    fun handleButtonClicked() {
        tt = TimeTask(this, "abc", object : TimeTask.Task {
            override fun exeTask() {
            }

            override fun period(): Long {
                return super.period()
            }
        })
    }
}