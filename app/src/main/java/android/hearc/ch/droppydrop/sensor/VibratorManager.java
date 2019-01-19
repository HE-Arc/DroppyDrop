package android.hearc.ch.droppydrop.sensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.hearc.ch.droppydrop.OptionsActivity;
import android.hearc.ch.droppydrop.R;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


/**
 * Source : https://developer.android.com/reference/android/os/Vibrator
 */
public class VibratorManager{

    private static final String TAG = VibratorManager.class.getSimpleName();

    private Vibrator vibrator;
    private Context context;
    private long[] pattern = {0, 150};
    private int amplitude = 1;
    private SharedPreferences sharedPreferences;



    public VibratorManager(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

    }

    public void startVibrator() {
        //Require API level 26 or above
        //Android does not allow to handle the intensity of the vibrator -> https://stackoverflow.com/questions/11483168/android-set-power-of-vibration
        vibrator.vibrate(amplitude);
    }

    public void stopVibrator()
    {
        vibrator.cancel();
    }

    public void setAmplitudeWithPreference() {
        //Value must be between 0 to 255 for amplitude
        int _amplitude = sharedPreferences.getInt(this.context.getString(R.string.vibration), 100);
        //Toast.makeText(this.context, Integer.toString(_amplitude), Toast.LENGTH_LONG).show();
        amplitude = _amplitude;
    }


}
