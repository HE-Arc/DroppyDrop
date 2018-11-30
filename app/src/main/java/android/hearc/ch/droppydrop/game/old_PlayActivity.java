package android.hearc.ch.droppydrop.game;

import android.hearc.ch.droppydrop.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class old_PlayActivity extends AppCompatActivity {

    private static final String TAG = old_PlayActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);

        GameManagerLayout game=(GameManagerLayout) findViewById(R.id.game);
        game.init(this,getIntent().getIntExtra("selectedLevel", 0),null);
    }
}
