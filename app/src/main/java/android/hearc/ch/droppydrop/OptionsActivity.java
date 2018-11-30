package android.hearc.ch.droppydrop;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        SharedPreferences sharedPref = getPreferences(this.MODE_PRIVATE);

        String default_username = getResources().getString(R.string.default_username);
        String username = sharedPref.getString(getString(R.string.username), default_username);
        EditText usernameEditText = findViewById(R.id.username_editText);
        usernameEditText.setText(username);
    }

    private void savePreferences()
    {
        SharedPreferences sharedPref = getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText usernameEditText = findViewById(R.id.username_editText);
        String newUsername = usernameEditText.getText().toString();
        editor.putString(getString(R.string.username), newUsername);
        editor.commit();
    }
}
