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
    private boolean isPause;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int levelId = getIntent().getIntExtra("selectedLevel", 0);
        game = new GameView(this, levelId);
        isPause = true;
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
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(this, "ON PAUSE", Toast.LENGTH_LONG).show();
                break;
            case MotionEvent.ACTION_UP:
                game.setOnPause();
                new AlertDialog.Builder(this)
                        .setTitle("Game on pause")
                        .setMessage("Reprendre la partie ?")
                        .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                game.setOnResume();
                            }
                        })
                        .setNegativeButton("Quit game", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert
                        ).show();
                break;
        }
        return super.onTouchEvent(event);
    }

}
