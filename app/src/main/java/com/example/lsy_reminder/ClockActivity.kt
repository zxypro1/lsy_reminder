package com.example.lsy_reminder

//noinspection SuspiciousImport
import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class ClockActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.dice)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
        mediaPlayer!!.start()
        //创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
        AlertDialog.Builder(this@ClockActivity).setTitle("闹钟").setMessage("LSY快起床!")
            .setPositiveButton("关闭闹铃", DialogInterface.OnClickListener { _, _ ->
                mediaPlayer!!.stop()
                finish()
            }).show()
    }
}