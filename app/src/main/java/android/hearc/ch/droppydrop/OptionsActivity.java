package android.hearc.ch.droppydrop;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        OptionsActivity.sharedPreferences = getPreferences(this.MODE_PRIVATE);

        loadAndDisplayPreferences();

        // Sensibility
        SeekBar sensibilitySeekBar = findViewById(R.id.sensibility_seekBar);
        sensibilitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // nothing to do
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing to do
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = OptionsActivity.sharedPreferences.edit();
                int newSensibility = seekBar.getProgress();
                editor.putInt(getString(R.string.sensibility), newSensibility);
                editor.commit();
            }
        });

        // Vibration
        SeekBar vibrationSeekBar = findViewById(R.id.vibration_seekBar);
        vibrationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // nothing to do
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing to do
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = OptionsActivity.sharedPreferences.edit();
                int newVibration = seekBar.getProgress();
                editor.putInt(getString(R.string.vibration), newVibration);
                editor.commit();
            }
        });

        EditText usernameEditText = findViewById(R.id.username_editText);

        usernameEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {
                SharedPreferences.Editor editor = OptionsActivity.sharedPreferences.edit();
                String newUsername = c.toString();
                editor.putString(getString(R.string.username), newUsername);

                editor.commit();
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // nothing to be done
            }

            public void afterTextChanged(Editable c) {
                // nothing to be done
            }
        });
    }

    private void loadAndDisplayPreferences()
    {
        // https://developer.android.com/training/data-storage/shared-preferences
        SharedPreferences sharedPref = OptionsActivity.sharedPreferences;

        // Sensibility
        int default_sensibility = 3;
        int sensibility = sharedPref.getInt(getString(R.string.sensibility), default_sensibility);
        SeekBar sensibilitySeekBar = findViewById(R.id.sensibility_seekBar);
        sensibilitySeekBar.setProgress(sensibility);

        // Vibration
        int default_vibration = 3;
        int vibration = sharedPref.getInt(getString(R.string.vibration), default_vibration);
        SeekBar vibrationSeekBar = findViewById(R.id.vibration_seekBar);
        vibrationSeekBar.setProgress(vibration);

        // Username
        String default_username = getResources().getString(R.string.default_username);
        String username = sharedPref.getString(getString(R.string.username), default_username);
        EditText usernameEditText = findViewById(R.id.username_editText);
        usernameEditText.setText(username);
    }
}
