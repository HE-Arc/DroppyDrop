package android.hearc.ch.droppydrop;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        loadAndDisplayPreferences();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
            }
        });
    }

    private void loadAndDisplayPreferences()
    {
        // https://developer.android.com/training/data-storage/shared-preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Sensibility
        int default_sensibility = 3;
        int sensibility = sharedPreferences.getInt(getString(R.string.sensibility), default_sensibility);
        SeekBar sensibilitySeekBar = findViewById(R.id.sensibility_seekBar);
        sensibilitySeekBar.setProgress(sensibility);

        // Vibration
        int default_vibration = 3;
        int vibration = sharedPreferences.getInt(getString(R.string.vibration), default_vibration);
        SeekBar vibrationSeekBar = findViewById(R.id.vibration_seekBar);
        vibrationSeekBar.setProgress(vibration);

        // Username
        String default_username = getResources().getString(R.string.default_username);
        String username = sharedPreferences.getString(getString(R.string.username), default_username);
        EditText usernameEditText = findViewById(R.id.username_editText);
        usernameEditText.setText(username);
    }

    private void savePreferences()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Sensibility
        SeekBar sensibilitySeekBar = findViewById(R.id.sensibility_seekBar);
        int newSensibility = sensibilitySeekBar.getProgress();
        editor.putInt(getString(R.string.sensibility), newSensibility);

        // Vibration
        SeekBar vibrationSeekBar = findViewById(R.id.vibration_seekBar);
        int newVibration = vibrationSeekBar.getProgress();
        editor.putInt(getString(R.string.vibration), newVibration);

        // Username
        EditText usernameEditText = findViewById(R.id.username_editText);
        String newUsername = usernameEditText.getText().toString();
        editor.putString(getString(R.string.username), newUsername);

        editor.apply();
    }
}
