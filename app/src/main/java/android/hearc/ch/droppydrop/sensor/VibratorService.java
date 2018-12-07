package android.hearc.ch.droppydrop.sensor;

import android.app.Service;
import android.content.Intent;
import android.hearc.ch.droppydrop.sensor.VibratorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class VibratorService extends Service {

    private static final String TAG = "Vibrator Service";
    private VibratorManager vibratorManager;


    @Override
    public void onCreate() {
        //Toast.makeText(this, "Mon service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        vibratorManager = new VibratorManager(this);
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Mon service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        vibratorManager.stopVibrator();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Mon service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        vibratorManager.startVibrator();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
