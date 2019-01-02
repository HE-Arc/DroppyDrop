package android.hearc.ch.droppydrop.game;

import android.content.Intent;
import android.hearc.ch.droppydrop.R;
import android.hearc.ch.droppydrop.sensor.VibratorService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = PlayActivity.class.getSimpleName();
    private GameManagerLayout game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_play);

        game=(GameManagerLayout) findViewById(R.id.game);
        game.init(this,getIntent().getIntExtra("selectedLevel", 0),null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        this.stopService(new Intent(this, VibratorService.class));
        game.destroy();
    }
}
