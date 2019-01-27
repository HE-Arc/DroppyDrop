package android.hearc.ch.droppydrop.game.Level;

import android.content.Intent;
import android.hearc.ch.droppydrop.game.PlayActivity;
import android.hearc.ch.droppydrop.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Level Activity
 */
public class LevelActivity extends AppCompatActivity {

    private List<Button> levelButtons;
    private List<LevelModel> levelModels;
    private int selectedLevel;


    /**
     * Create Level Activity
     * Add buttons for each level contains in res/values/levels.xmls
     * @param savedInstanceState state of the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        levelButtons = new ArrayList<Button>();
        levelModels = new ArrayList<LevelModel>();

        int buttonsPerRow = 2;
        int buttonCounter = 0;
        int levelCount = getResources().getStringArray(R.array.names).length;

        TableLayout table = (TableLayout) findViewById(R.id.levelTableLayout);

        for (int i = 0; i < Math.ceil(levelCount / (double) buttonsPerRow); i++) {

            // Add a new row
            TableRow tr = new TableRow(this);
            tr.setGravity(Gravity.CENTER_HORIZONTAL);
            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            table.addView(tr, table.getChildCount() - buttonsPerRow);

            // Add 3 levels per row
            for (int x = 0; x < buttonsPerRow; x++) {

                Button button = new Button(new ContextThemeWrapper(this, R.style.AppTheme_button));
                levelModels.add(new LevelModel(this, buttonCounter));

                // set text
                String levelName = levelModels.get(buttonCounter).LevelName;
                Log.i("BUTTON", "onCreate: " + levelName + "/");
                button.setText(levelName);

                // Increment button counter
                buttonCounter++;

                // Add on click listener
                button.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Display level info
                        int levelId = levelButtons.indexOf(view);
                        selectedLevel = levelId;
                        int trackColor = levelModels.get(levelId).TrackColorInt;
                        int difficulty = levelModels.get(levelId).Difficulty;
                        int imageId = levelModels.get(levelId).ImageId;

                        // Set info of level
                        TextView levelInfo = (TextView) findViewById(R.id.levelInfo);

                        String beforeDropColorString = "Difficulty: " + difficulty + "\nDrop color: ";
                        Spannable spanna = new SpannableString(beforeDropColorString + "    ");
                        spanna.setSpan(new BackgroundColorSpan(trackColor), beforeDropColorString.length(), beforeDropColorString.length() + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        levelInfo.setText(spanna);

                        // Set Image of level
                        ImageView levelImage = (ImageView) findViewById(R.id.levelImage);
                        levelImage.setImageResource(imageId);

                        Button playButton = (Button) findViewById(R.id.playLevelButton);
                        playButton.setVisibility(View.VISIBLE);
                    }
                });

                // Add button in the layout
                tr.addView(button);
                levelButtons.add(button);
                if (buttonCounter == levelCount)
                    break;
            }
        }

        // Add event on play button
        Button playButton = (Button) findViewById(R.id.playLevelButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We have to find a way to pass the level information
                Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                intent.putExtra("selectedLevel", selectedLevel);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
