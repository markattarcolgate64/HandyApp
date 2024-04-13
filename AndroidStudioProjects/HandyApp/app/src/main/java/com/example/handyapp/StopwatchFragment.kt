package com.example.handyapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay

class StopwatchFragment: Fragment() {

    private lateinit var startStopButton: ToggleButton
    private lateinit var resetButton: Button
    private lateinit var timeText: TextView
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    private var timeElapsed = 0L
    private var timeBuffer= 0L
    private var isRunning = false
    private var time = 0L




    private var runnable : Runnable = object: Runnable{

        override fun run() {
            //Access system uptime and find what amount has gone from that since the start
            //So its like

            time = System.currentTimeMillis() - startTime + timeBuffer
            var secs = (time/1000).toInt()
            var mins = secs/60
            var milliseconds = (time%1000).toInt()
            secs = secs % 60

            timeText.text = String.format("%02d:%02d:%02d",mins,secs, milliseconds /10)
            timeElapsed = time
            if (isRunning != true){
                isRunning = true
            }
            handler.postDelayed(this,100)
        }

        fun isRunning(): Boolean{
            return isRunning
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeBuffer", timeBuffer)
        outState.putLong("startTime", startTime)
        outState.putLong("timeElapsed", timeElapsed)
        outState.putCharSequence("currentTime", timeText.text)

    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null){
            startTime = savedInstanceState.getLong("startTime")
            timeElapsed = savedInstanceState.getLong("timeElapsed")
            timeBuffer = savedInstanceState.getLong("timeBuffer")
            timeText.text = savedInstanceState.getCharSequence("currentTime")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
//Focus what is next for stopwatch?
    ): View? {
        val view = inflater.inflate(R.layout.stopwatch_fragment, container, false)

        startStopButton = view.findViewById(R.id.start_stop_stopwatch)
        resetButton = view.findViewById(R.id.reset_stopwatch)
        timeText = view.findViewById(R.id.time_text)
        startStopButton.setOnCheckedChangeListener { startStopButton, isChecked ->

            if (isChecked){
                startTime = System.currentTimeMillis()
                handler.postDelayed(runnable, 100)
            } else{
                timeBuffer += timeElapsed
                handler.removeCallbacks(runnable)
                isRunning = false
            }
        }

        resetButton.setOnClickListener{resetButtonFun()}

        return view
    }


    private fun resetButtonFun(){
        //just set the seconds to zero
        if (isRunning){
           startTime = System.currentTimeMillis() + timeBuffer
        } else {
            timeBuffer = 0L
            timeText.text = getString(R.string.stopwatchString)
        }

    }
}