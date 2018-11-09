package android.hearc.ch.droppydrop;

import android.content.Context;
import android.os.Vibrator;

/**
 * Source : https://developer.android.com/reference/android/os/Vibrator
 */
public class VibratorManager{

    private static final String TAG = VibratorManager.class.getSimpleName();

    private Vibrator vibrator;
    private Context context;
    private long[] pattern = {0, 500,0};

    public VibratorManager(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void startVibrator() {
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrator()
    {
        vibrator.cancel();
    }


}
