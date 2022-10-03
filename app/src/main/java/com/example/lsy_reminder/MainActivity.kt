package com.example.lsy_reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener


class MainActivity : AppCompatActivity() {
    private var pi: PendingIntent? = null
    private var alarmManager: AlarmManager? = null
    private var sleepButton: Button? = null
    private var cancelButton: Button? = null
    private var sleepHours: EditText? = null
    private var sleep_hours: Long = 0
    private var hasSetSleepHours: Boolean = false
    private var sleepText: TextView? = null
    private lateinit var tt : TimeTask<TimeTask.Task>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        pi = PendingIntent.getActivity(this@MainActivity, 0, intent, 0)

        sleepButton = findViewById<Button>(R.id.sleep_button)
        sleepHours = findViewById<EditText>(R.id.sleep_hours)
        sleepText = findViewById<TextView>(R.id.sleep_text)
        cancelButton = findViewById<Button>(R.id.cancel_button)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        updateUI()

        sleepHours?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(editable: Editable) {
                var content:String=editable.toString()
                if(content.contains("\r")||content.contains("\n")){
                    //去掉回车与换行
                    content = content.replace("\r","").replace("\n","")
                }
                if (content == "") {
                    sleep_hours = 0L
                } else {
                    sleep_hours = content.toLong()
                }
            }
        })
        cancelButton?.setOnClickListener {
            try {
                handleStopButtonClicked()
            } catch (error: Throwable) {
                Toast.makeText(this, "未能成功取消闹钟! 请查看日志", Toast.LENGTH_SHORT).show()
            }
        }
        sleepButton?.setOnClickListener {
            try {
                handleStartButtonClicked()
            } catch (error: Throwable) {
                Toast.makeText(this, "未能成功创建闹钟! 请查看日志", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun handleStartButtonClicked() {
        if (sleep_hours > 0L) {
            tt = TimeTask(this, "abc", object : TimeTask.Task {
                override fun exeTask() {
                    val intent = Intent(this@MainActivity, ClockActivity::class.java)
                    hasSetSleepHours = false
                    updateUI()
//                pi = PendinIntent.getActivity(this@MainActivity, 0, intent, 0)
                    startActivity(intent)
                }

                override fun period(): Long {
                    return sleep_hours * 100L * 60L * 60L
                }
            })
            tt.startLooperTask()
            hasSetSleepHours = true
            updateUI()
            Toast.makeText(this, "成功创建闹钟", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "请输入大于零的整数", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleStopButtonClicked() {
        tt.stopLooperTask()
        hasSetSleepHours = false
        updateUI()
        Toast.makeText(this, "成功取消闹钟", Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        if (hasSetSleepHours) {
            sleepText!!.text = "已设置${sleep_hours}小时后的闹钟，马上睡觉！"
        } else {
            sleepText!!.text = "暂未设置闹钟"
        }
    }
}