package com.trios2025rm.androidassignment1

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import android.content.SharedPreferences

class OptionsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        sharedPreferences = getSharedPreferences("game_settings", MODE_PRIVATE)

        // Initialize UI elements
        val buttonBack = findViewById<ImageButton>(R.id.buttonBackFromOptions)
        val switchVibration = findViewById<Switch>(R.id.switchVibration)
        val switchSound = findViewById<Switch>(R.id.switchSound)
        val seekBarTimer = findViewById<SeekBar>(R.id.seekBarTimer)
        val textViewTimerValue = findViewById<TextView>(R.id.textViewTimerValue)

        // Load saved preferences
        switchVibration.isChecked = sharedPreferences.getBoolean("vibration_enabled", true)
        switchSound.isChecked = sharedPreferences.getBoolean("sound_enabled", true)
        val savedTimer = sharedPreferences.getInt("timer_duration", 30)
        seekBarTimer.progress = savedTimer - 10 // Convert from seconds to progress
        textViewTimerValue.text = getString(R.string.timer_duration_value, savedTimer)

        // Set up back button
        buttonBack.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Set up vibration switch
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("vibration_enabled", isChecked).apply()
        }

        // Set up sound switch
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply()
        }

        // Set up timer duration seekbar
        seekBarTimer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val timerValue = progress + 10 // Convert progress to seconds (10-60 range)
                textViewTimerValue.text = getString(R.string.timer_duration_value, timerValue)
                sharedPreferences.edit().putInt("timer_duration", timerValue).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 