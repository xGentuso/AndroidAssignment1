package com.trios2025rm.androidassignment1

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.CountDownTimer

/**
 * MainActivity class for the Enhanced Click Counter App
 * Features:
 * - Animated counter changes
 * - Haptic feedback
 * - Special achievements
 * - Combo system
 * - Background color transitions
 */
class MainActivity : AppCompatActivity() {

    // Variables to track the counter state
    private var count = 0
    private var combo = 0
    private var lastClickTime = 0L
    private val comboTimeout = 1000L // 1 second for combo
    private val achievements = mapOf(
        100 to "Century Club! 🏆",
        50 to "Half Century! 🌟",
        25 to "Quarter Century! ⭐",
        10 to "Double Digits! 🎯",
        5 to "Getting Started! 🚀"
    )

    // Timer variables
    private var isTimerMode = false
    private var timerTapCount = 0
    private var countDownTimer: CountDownTimer? = null
    private val timerDuration = 30000L // 30 seconds
    private val timerInterval = 1000L // 1 second updates

    // UI Elements
    private lateinit var textViewCount: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var textViewCombo: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var buttonIncrease: Button
    private lateinit var buttonDecrease: Button
    private lateinit var buttonReset: Button
    private lateinit var buttonTimer: Button
    private lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize the UI elements
        textViewCount = findViewById(R.id.textViewCount)
        textViewStatus = findViewById(R.id.textViewStatus)
        textViewCombo = findViewById(R.id.textViewCombo)
        textViewTimer = findViewById(R.id.textViewTimer)
        buttonIncrease = findViewById(R.id.buttonIncrease)
        buttonDecrease = findViewById(R.id.buttonDecrease)
        buttonReset = findViewById(R.id.buttonReset)
        buttonTimer = findViewById(R.id.buttonTimer)
        mainLayout = findViewById(R.id.main)

        // Initialize back button
        val buttonBackToMenu = findViewById<ImageButton>(R.id.buttonBackToMenu)
        buttonBackToMenu.setOnClickListener {
            // Add button press animation
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            
            // Return to start screen with animation
            finish()
            animateExit()
        }

        // Set up back press handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                animateExit()
            }
        })

        // Set edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up button click listeners
        setupClickListeners()

        // Initialize the UI with the current count
        updateUI()
    }

    private fun setupClickListeners() {
        buttonIncrease.setOnClickListener {
            if (isTimerMode) {
                timerTapCount++
                textViewCount.text = timerTapCount.toString()
                animateCounter()
                provideHapticFeedback()
            } else {
                handleClick(true)
            }
        }

        buttonDecrease.setOnClickListener {
            if (!isTimerMode) {
                handleClick(false)
            }
        }

        buttonReset.setOnClickListener {
            if (!isTimerMode) {
                resetCounter()
            }
        }

        buttonTimer.setOnClickListener {
            if (!isTimerMode) {
                startTimerMode()
            }
        }
    }

    private fun handleClick(isIncrease: Boolean) {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastClickTime < comboTimeout) {
            combo++
        } else {
            combo = 1
        }
        lastClickTime = currentTime

        if (isIncrease) {
            count++
        } else {
            count--
        }

        provideHapticFeedback()
        animateCounter()
        updateUI()
    }

    private fun startTimerMode() {
        isTimerMode = true
        timerTapCount = 0
        textViewCount.text = "0"
        textViewStatus.text = ""
        textViewCombo.text = ""
        
        // Disable unnecessary buttons during timer mode
        buttonDecrease.isEnabled = false
        buttonReset.isEnabled = false
        buttonTimer.isEnabled = false

        // Start the countdown timer
        countDownTimer = object : CountDownTimer(timerDuration, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                textViewTimer.text = getString(R.string.timer_countdown, secondsLeft)
                
                // Flash the timer text when 5 seconds or less remain
                if (secondsLeft <= 5) {
                    val animation = AlphaAnimation(0.0f, 1.0f)
                    animation.duration = 500
                    animation.repeatMode = Animation.REVERSE
                    animation.repeatCount = 1
                    textViewTimer.startAnimation(animation)
                }
            }

            override fun onFinish() {
                endTimerMode()
            }
        }.start()
    }

    private fun endTimerMode() {
        isTimerMode = false
        countDownTimer = null
        
        // Show final score
        textViewTimer.text = getString(R.string.timer_result, timerTapCount)
        
        // Re-enable buttons
        buttonDecrease.isEnabled = true
        buttonReset.isEnabled = true
        buttonTimer.isEnabled = true
        
        // Reset to normal mode
        count = 0
        updateUI()
    }

    private fun resetCounter() {
        val animator = ValueAnimator.ofInt(count, 0)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        
        animator.addUpdateListener { animation ->
            count = animation.animatedValue as Int
            updateUI()
        }
        
        animator.start()
        combo = 0
    }

    private fun updateUI() {
        // Update the count display with combo
        textViewCount.text = count.toString()
        textViewCombo.text = if (combo > 1) "Combo: x$combo" else ""

        // Check for achievements
        val achievement = achievements[count]
        if (achievement != null) {
            textViewStatus.text = achievement
            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.milestone_color))
            // Animate achievement text
            val achievementAnimation = AnimationUtils.loadAnimation(this, R.anim.achievement_animation)
            textViewStatus.startAnimation(achievementAnimation)
        } else {
            // Set status message and background color based on count value
            when {
                count > 0 -> {
                    textViewStatus.text = getString(R.string.count_positive)
                    animateBackgroundColor(ContextCompat.getColor(this, R.color.positive_color))
                }
                count < 0 -> {
                    textViewStatus.text = getString(R.string.count_negative)
                    animateBackgroundColor(ContextCompat.getColor(this, R.color.negative_color))
                }
                else -> {
                    textViewStatus.text = getString(R.string.count_zero)
                    animateBackgroundColor(ContextCompat.getColor(this, R.color.neutral_color))
                }
            }
        }

        // Adjust text colors for visibility against the background
        if (count < 0 || count == 0) {
            textViewCount.setTextColor(Color.WHITE)
            textViewStatus.setTextColor(Color.WHITE)
            textViewCombo.setTextColor(Color.WHITE)
        } else {
            textViewCount.setTextColor(Color.BLACK)
            textViewStatus.setTextColor(Color.BLACK)
            textViewCombo.setTextColor(Color.BLACK)
        }
    }

    private fun animateCounter() {
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)
        textViewCount.startAnimation(scaleAnimation)
    }

    private fun animateBackgroundColor(targetColor: Int) {
        val currentColor = (mainLayout.background as? android.graphics.drawable.ColorDrawable)?.color 
            ?: ContextCompat.getColor(this, R.color.neutral_color)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val animator = ValueAnimator.ofArgb(currentColor, targetColor)
            animator.duration = 300
            animator.addUpdateListener { animation ->
                mainLayout.setBackgroundColor(animation.animatedValue as Int)
            }
            animator.start()
        } else {
            // For older versions, just set the color without animation
            mainLayout.setBackgroundColor(targetColor)
        }
    }

    private fun provideHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50)
                }
            }
        } catch (e: Exception) {
            // Silently handle vibration errors
        }
    }

    private fun animateExit() {
        @Suppress("DEPRECATION")
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}