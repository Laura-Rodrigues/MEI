package com.example.mysensorapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    // Get sensor manager
    lateinit var sensorManager : SensorManager
    // Get the accelerometer sensor
    lateinit var mAccelerometer : Sensor
    lateinit var accelerometerSensorListener : AccelerometerSensorListener
    private val model: AccelerometerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Get the accelerometer sensor
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // If the smartphone has this sensor
        if (mAccelerometer != null){
            accelerometerSensorListener = AccelerometerSensorListener()
            accelerometerSensorListener.setSensorManager(sensorManager, model)
            sensorManager.registerListener(accelerometerSensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        }
        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener{
            Log.d("BUTTONS", "User clicked button UNREGISTER")
            stopListener()
        }

        val button1 = findViewById<Button>(R.id.button)
        button1.setOnClickListener{
            Log.d("BUTTONS", "User clicked button REGISTER")
            restartListener()
        }

        val accelerometerObserver = Observer<AccelerometerData> { newSample ->
            // Update the UI, in this case, the X, Y and Z textviews
            findViewById<TextView>(R.id.textView3).text = newSample.valueX.toString()
            findViewById<TextView>(R.id.textView4).text = newSample.valueY.toString()
            findViewById<TextView>(R.id.textView5).text = newSample.valueZ.toString()
        }
        model.currentAccelerometerData.observe(this, accelerometerObserver)
    }

    fun stopListener(){

        accelerometerSensorListener.unregisterListener()
    }

    fun restartListener(){
        accelerometerSensorListener.registerListener(mAccelerometer)
    }
}