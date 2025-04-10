package com.trios2025rm.androidassignment1

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        // Initialize UI elements
        val titleText = findViewById<TextView>(R.id.titleText)
        val startButton = findViewById<Button>(R.id.startButton)
        val optionsButton = findViewById<Button>(R.id.optionsButton)
        val exitButton = findViewById<Button>(R.id.exitButton)

        // Apply entrance animation to title
        titleText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        // Set up button click listeners
        startButton.setOnClickListener {
            // Start game activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        optionsButton.setOnClickListener {
            // Start options activity
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        exitButton.setOnClickListener {
            // Exit the app
            finish()
        }

        // Apply button animations
        val buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        startButton.startAnimation(buttonAnimation)
        optionsButton.startAnimation(buttonAnimation)
        exitButton.startAnimation(buttonAnimation)
    }
} 