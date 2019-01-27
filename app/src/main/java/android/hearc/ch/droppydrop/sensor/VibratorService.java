package android.hearc.ch.droppydrop.sensor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Classe for handling the service of the vibrator
 */
public class VibratorService extends Service {

    private static final String TAG = "Vibrator Service";

    private VibratorManager vibratorManager;

    @Override
    public void onCreate() {
        vibratorManager = new VibratorManager(this);
        vibratorManager.setAmplitudeWithPreference();
    }

    @Override
    public void onDestroy() {
        vibratorManager.stopVibrator();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vibratorManager.startVibrator();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
