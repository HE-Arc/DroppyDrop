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
 * Classe for handling the vibration
 */
public class VibratorManager{

    private static final String TAG = VibratorManager.class.getSimpleName();

    private Vibrator vibrator;
    private Context context;
    private int amplitude = 1;
    private SharedPreferences sharedPreferences;


    /**
     * Instanciation of the vibrator and sharedPreferences object
     * @param context
     */
    public VibratorManager(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }


    /**
     * Start the vibration of vibrator
     */
    public void startVibrator() {
        //Android does not allow to handle the intensity of the vibrator -> https://stackoverflow.com/questions/11483168/android-set-power-of-vibration
        vibrator.vibrate(amplitude);
    }

    /**
     * Stop the vibration of vibrator
     */
    public void stopVibrator()
    {
        vibrator.cancel();
    }

    /**
     * Set amplitude with the Vibration SharedPreference parameter
     */
    public void setAmplitudeWithPreference() {
        //Value must be between 0 to 255 for amplitude
        int _amplitude = sharedPreferences.getInt(this.context.getString(R.string.vibration), 127);
        amplitude = _amplitude;
    }


}
