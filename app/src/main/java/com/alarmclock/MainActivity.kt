package com.alarmclock

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.alarmclock.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()

        binding.selectTime.setOnClickListener {

            showTimePicker()


        }
        binding.setalarmbtn.setOnClickListener {

            setAlarm()

        }
        binding.cancelalarmbtn.setOnClickListener {

            cancelAlarm()
        }
    }

    private fun cancelAlarm() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(this,"Alarm Cancelled",Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setAlarm() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(

            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )

        Toast.makeText(this,"Alarm set Successfuly",Toast.LENGTH_SHORT).show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showTimePicker() {

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "AlarmClock")

        picker.addOnPositiveButtonClickListener {

            if (picker.hour > 12){

                binding.selectedTime.text =
                    String.format("%02d",picker.hour - 12) + " : " + String.format(
                        "%02d",
                        picker.minute
                    ) + "PM"
            } else{

                String.format("%02d",picker.hour - 12) + " : " + String.format(
                    "%02d",
                    picker.minute
                ) + "AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name : CharSequence = "AlarmClockRemainderchannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("AlarmClock", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }
    }
}