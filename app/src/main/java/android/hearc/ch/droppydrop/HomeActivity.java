package android.hearc.ch.droppydrop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    Button playButton;
    Button scoreButton;
    Button optionsButton;
    Button creditsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        playButton = (Button) findViewById(R.id.playButton);
        scoreButton = (Button) findViewById(R.id.scoreButton);
        optionsButton = (Button) findViewById(R.id.optionsButton);
        creditsButton = (Button) findViewById(R.id.creditsButton);

        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                // TODO show LevelActivity
            }
        });
        scoreButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO show ScoreActivity
            }
        });
        optionsButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO show OptionsActivity
            }
        });
        creditsButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO popup credits
            }
        });
    }
}
