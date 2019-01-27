package android.hearc.ch.droppydrop.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.hearc.ch.droppydrop.sensor.VibratorService;

public class PlayActivity extends Activity {

    private GameView game;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int levelId = getIntent().getIntExtra("selectedLevel", 0);
        game = new GameView(this, levelId);
        setContentView(game);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        this.stopService(new Intent(this, VibratorService.class));
        game.destroy();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_UP:
                pauseMenu();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void pauseMenu()
    {
        game.setOnPauseFromDialog();

        new AlertDialog.Builder(this)
                .setTitle("PAUSE")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setNeutralButton("Resume", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.setOnResumeFromDialog();
                    }
                })
                .setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mIntent = getIntent();
                        finish();
                        startActivity(mIntent);
                        game.destroy();

                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.star_big_off)
                .show();
    }




}
