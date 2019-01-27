package android.hearc.ch.droppydrop.sensor;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hearc.ch.droppydrop.sensor.VibratorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Classe for handling the service of the vibrator
 */
public class VibratorService extends Service {

    private static final String TAG = "Vibrator Service";

    private VibratorManager vibratorManager;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        vibratorManager = new VibratorManager(this);
        vibratorManager.setAmplitudeWithPreference();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        vibratorManager.stopVibrator();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
