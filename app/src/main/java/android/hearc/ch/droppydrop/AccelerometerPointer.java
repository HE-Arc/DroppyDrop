package android.hearc.ch.droppydrop;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Source : https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
 */
public class AccelerometerPointer implements SensorEventListener {

    private static final String TAG = "GAME";//AccelerometerPointer.class.getSimpleName();

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private Point origin;
    private Point acceleration;
    private Point pointer;

    public AccelerometerPointer(Context context) {
        super();

        Log.i(TAG, "AccelerometerPointer: is in constructor");

        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            Log.i(TAG, "onSensorChanged: values :" + x + ' ' + y + ' ' + z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
