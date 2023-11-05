package com.example.mysensorapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


class AccelerometerSensorListener: SensorEventListener {
    companion object {
        private const val TAG: String = "AccelerometerSensorL"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var viewModel : AccelerometerViewModel

    fun setSensorManager(sensorMan: SensorManager, vModel : AccelerometerViewModel) {
        sensorManager = sensorMan
        viewModel = vModel
    }

    override fun onSensorChanged(event: SensorEvent) {
        AccelerometerData.valueX = event.values[0]
        AccelerometerData.valueY = event.values[1]
        AccelerometerData.valueZ = event.values[2]
        AccelerometerData.accuracy = event.accuracy
        //sensorManager.unregisterListener(this)
        viewModel.currentAccelerometerData.value = AccelerometerData


        Log.d(TAG,
            "[SENSOR] - X=${AccelerometerData.valueX}, Y=${AccelerometerData.valueY}, Z=${AccelerometerData.valueZ}"
        )
    }

    fun unregisterListener(){
        sensorManager.unregisterListener(this)
    }

    fun registerListener(s : Sensor){
        sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


}