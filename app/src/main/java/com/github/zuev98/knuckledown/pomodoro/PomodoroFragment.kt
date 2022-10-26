package com.github.zuev98.knuckledown.pomodoro

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.github.zuev98.knuckledown.R
import com.github.zuev98.knuckledown.databinding.FragmentPomodoroBinding
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.*
import java.util.concurrent.TimeUnit

private const val DEFAULT_TIME = 1_500_000L //25 minutes
private const val BIG_DEFAULT_TIME = 3_000_000L //50 minutes
private const val BREAK_TIME = 300_000L //5 minutes
private const val BIG_BREAK_TIME = 1_200_000L //20 minutes
private const val MINUTES_TO_MS = 60 * 1000 //Coefficient for converting minutes to milliseconds

//Pomodoro-timer resets when configuration or state changes
//The user should not use the phone when the timer is running
class PomodoroFragment : Fragment() {
    private enum class TimerState {
        Running, Paused, Breaking
    }

    private var _binding: FragmentPomodoroBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private var timer: CountDownTimer? = null
    private var timerState = TimerState.Paused
    private var timeLeftInMillis = DEFAULT_TIME
    private var currentTime = 0L
    private var currentBreakTime = 0L
    private var pomodoroCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)

        setTimerTime()

        binding.apply {
            timerEditText.doAfterTextChanged {
                if (timerState == TimerState.Paused) {
                    setTimerTime()
                }
            }

            startFab.setOnClickListener {
                if (timerState != TimerState.Paused) {
                    todoHint.text = getString(R.string.start_timer)
                    startFab.setImageResource(R.drawable.ic_start_24)
                    startFab.contentDescription = getString(R.string.start_timer_description)
                    resetTimer()
                } else {
                    todoHint.text = getString(R.string.focus_on_work)
                    startFab.setImageResource(R.drawable.ic_stop_24)
                    startFab.contentDescription = getString(R.string.stop_timer_description)
                    startTimer()
                }
                progressCircular.progress = 0
            }

            breakFab.setOnClickListener {
                currentBreakTime = timeLeftInMillis
                timeLeftInMillis = currentTime
                if (timerState == TimerState.Breaking) {
                    startTimer()
                }
                progressCircular.progress = 0
                breakFab.isEnabled = false
            }
        }

        updCountdownText(timeLeftInMillis)

        return binding.root
    }

    private fun setTimerTime() {
        timeLeftInMillis = binding.timerEditText.text.toString()
            .toLongOrNull()?.times(MINUTES_TO_MS) ?: DEFAULT_TIME
        isTimeCorrect()
        updCountdownText(timeLeftInMillis)
    }

    private fun isTimeCorrect() {
        if (timeLeftInMillis < DEFAULT_TIME) {
            timeLeftInMillis = DEFAULT_TIME
        } else if (timeLeftInMillis > BIG_DEFAULT_TIME) {
            timeLeftInMillis = BIG_DEFAULT_TIME
        }
    }

    private fun updCountdownText(time: Long) {
        binding.timerText.text = formattedTime(time)
    }

    private fun formattedTime(time: Long): String {
        val minutes = (time / 1000).toInt() / 60
        val seconds = (time / 1000).toInt() % 60

        return String.format(Locale("Ru"),"%02d:%02d", minutes, seconds)
    }

    private fun startTimer() {
        if (timerState == TimerState.Paused)
            timerState = TimerState.Running

        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            val numberOfSteps = timeLeftInMillis / 1000
            var stepNumber = 0

            override fun onTick(milliesUntilFinished: Long) {
                currentTime = milliesUntilFinished
                updCountdownText(currentTime)
                stepNumber++
                binding.progressCircular.progress = (stepNumber * 100.0 / (numberOfSteps)).toInt()
            }

            override fun onFinish() {
                if (timerState == TimerState.Running) {
                    timerState = TimerState.Breaking
                    binding.breakFab.isEnabled = true
                    binding.todoHint.text = getString(R.string.time_to_rest)
                    //Every 4th break is big
                    if (pomodoroCount != 3) {
                        currentTime = BREAK_TIME
                        pomodoroCount++
                    }
                    else {
                        currentTime = BIG_BREAK_TIME
                        pomodoroCount = 0
                    }

                    updCountdownText(currentTime)
                } else if (timerState == TimerState.Breaking) {
                    timerState = TimerState.Paused
                    currentTime = currentBreakTime
                    binding.apply {
                        startFab.setImageResource(R.drawable.ic_start_24)
                        startFab.contentDescription = getString(R.string.start_timer_description)
                        todoHint.text = getString(R.string.start_timer)
                    }

                    setTimerTime()
                }

                loadKonfetti()
            }
        }.start()
    }

    private fun resetTimer() {
        timer?.cancel()
        setTimerTime()
        timerState = TimerState.Paused
        pomodoroCount = 0
        currentTime = timeLeftInMillis
        updCountdownText(currentTime)
        binding.breakFab.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        if (timerState == TimerState.Running)
            updCountdownText(timeLeftInMillis)
        else if (currentBreakTime != 0L)
            updCountdownText(currentBreakTime)
        timerState = TimerState.Paused
        pomodoroCount = 0
        binding.apply {
            breakFab.isEnabled = false
            progressCircular.progress = 0
            startFab.setImageResource(R.drawable.ic_start_24)
            startFab.contentDescription = getString(R.string.start_timer_description)
        }
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        binding.breakFab.isEnabled = false
    }

    private fun loadKonfetti() {
        val party = Party(
            angle = Angle.BOTTOM,
            spread = 180,
            speed = 15f,
            damping = 0.95f,
            timeToLive = 5000L,
            position = Position.Relative(0.5, -0.05),
            rotation = Rotation.enabled(),
            emitter = Emitter(duration = 2000, TimeUnit.MILLISECONDS).max(800)
        )
        binding.konfettiView.start(party)
    }
}