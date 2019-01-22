package android.hearc.ch.droppydrop.sensor;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Source : https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
 */
public class AccelerometerPointer implements SensorEventListener {

    private static final String TAG = "ACC"; //AccelerometerPointer.class.getSimpleName();

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private Point origin;
    private Point pointer;

    public AccelerometerPointer(Context context, int height, int width) {
        super();

        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        origin = new Point();
        pointer = new Point();

        origin.x = width / 2;
        origin.y = height / 2;

        pointer.x = width / 2;
        pointer.y = height / 2;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            //float z = sensorEvent.values[2];
            synchronized (pointer) {
                pointer.x -= x;
                pointer.y += y;
            }

            //Log.i(TAG, "onSensorChanged: x: " + x + " y: "+ y + " z: "+ z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Reset the pointer to the center of the screen
     *
     * @param height
     * @param width
     */
    public void resetPointer(int height, int width) {

        origin.x = width / 2;
        origin.y = height / 2;

        pointer.x = width / 2;
        pointer.y = height / 2;
    }

    public Point getPointer() {
        //Log.i(TAG, "getPointer: " + pointer.toString());

        Point pointerCopy = new Point(pointer);
        return pointerCopy;
    }

    public void stopAccelerometerSensor() {
        senSensorManager.unregisterListener(this);
    }

    public void resumeAccelerometerSensor() {
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void setPointerX(int x) {
        synchronized (pointer) {
            pointer.x = x;
        }
    }

    public void setPointerY(int y) {
        synchronized (pointer) {
            pointer.y = y;
        }
    }


}
