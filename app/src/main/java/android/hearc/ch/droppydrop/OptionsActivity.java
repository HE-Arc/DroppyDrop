package android.hearc.ch.droppydrop;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // https://developer.android.com/training/data-storage/shared-preferences
        SharedPreferences sharedPref = getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String default_username = getResources().getString(R.string.default_username);
        String username = sharedPref.getString(getString(R.string.username), default_username);
        EditText usernameEditText = findViewById(R.id.username_editText);
        usernameEditText.setText(username);
    }
}
