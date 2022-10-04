package com.example.lsy_reminder

import android.annotation.SuppressLint
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {
    private var pi: PendingIntent? = null
    private var alarmManager: AlarmManager? = null
    private var sleepButton: Button? = null
    private var cancelButton: Button? = null
    private var sleepHours: EditText? = null
    private var sleep_hours: Long = 0
    private var hasSetSleepHours: Boolean = false
    private var sleepText: TextView? = null
    private lateinit var tt: TimeTask<TimeTask.Task>
    private val CHANNEL_ID: String = "lsy_reminder"
    private val notificationId: Int = 113435



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStop() {
        super.onStop()
        if (!isAppOnForeground() && hasSetSleepHours) {
            sendNotification()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendNotification() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with (NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sleepButton = findViewById<Button>(R.id.sleep_button)
        sleepHours = findViewById<EditText>(R.id.sleep_hours)
        sleepText = findViewById<TextView>(R.id.sleep_text)
        cancelButton = findViewById<Button>(R.id.cancel_button)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        /* ------------程序初始化------------------- */
        updateUI()
        createNotificationChannel()


        /* ------------设定触发器-------------------------*/
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
                sleep_hours = if (content == "") {
                    0L
                } else {
                    content.toLong()
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
    @RequiresApi(Build.VERSION_CODES.M)
    fun isAppOnForeground(): Boolean {
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = applicationContext.packageName
        val appProcesses = activityManager
            .runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.processName == packageName && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }
}