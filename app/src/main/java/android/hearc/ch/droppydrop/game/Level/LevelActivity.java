package android.hearc.ch.droppydrop.game.Level;

import android.content.Intent;
import android.hearc.ch.droppydrop.game.PlayActivity;
import android.hearc.ch.droppydrop.game.old_PlayActivity;
import android.hearc.ch.droppydrop.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity {

    private List<Button> levelButtons;
    private List<LevelModel> levelModels;
    private int selectedLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);


        levelButtons= new ArrayList<Button>();
        levelModels=new ArrayList<LevelModel>();

        int buttonsPerRow=3;
        int buttonCounter=0;
        int levelCount=getResources().getStringArray(R.array.names).length;


        TableLayout table = (TableLayout) findViewById(R.id.levelTableLayout);


        for (int i = 0; i < Math.ceil(levelCount/(double)buttonsPerRow); i++) {

            TableRow tr = new TableRow(this);
            LinearLayout linearLayout = new LinearLayout(this);
            tr.addView(linearLayout);
            table.addView(tr,table.getChildCount()-3);

                for (int x = 0; x < buttonsPerRow; x++) {

                        Button button = new Button(this);
                        levelModels.add(new LevelModel(this,buttonCounter));

                        String levelName=levelModels.get(buttonCounter).LevelName;

                        button.setText(levelName);
                        buttonCounter++;

                        button.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int levelId=levelButtons.indexOf(view);
                                selectedLevel=levelId;

                                int difficulty=levelModels.get(levelId).Difficulty;
                                int dropColor=levelModels.get(levelId).DropColorInt;
                                int imageId=levelModels.get(levelId).ImageId;


                                TableRow levelInfoRow = (TableRow)findViewById(R.id.levelInfoRow);
                                levelInfoRow.setVisibility(View.VISIBLE);
                                TableRow playLevel = (TableRow)findViewById(R.id.playLevelButtonRow);
                                playLevel.setVisibility(View.VISIBLE);
                                TableRow levelImageRow=(TableRow)findViewById(R.id.levelImageRow);
                                levelImageRow.setVisibility(View.VISIBLE);

                                TextView levelInfo=(TextView)findViewById(R.id.levelInfo);


                                String beforeDropColorString="Difficulty: "+difficulty+"\nDrop color: ";

                                Spannable spanna = new SpannableString(beforeDropColorString+"    ");
                                spanna.setSpan(new BackgroundColorSpan(dropColor),beforeDropColorString.length(), beforeDropColorString.length()+4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                levelInfo.setText(spanna);


                                ImageView levelImage =(ImageView)findViewById(R.id.levelImage);
                                levelImage.setImageResource(imageId);

                            }
                        });

                        linearLayout.addView(button);
                        levelButtons.add(button);
                        if(buttonCounter==levelCount)
                            break;
                    }
                }

                Button playButton=(Button)findViewById(R.id.playLevelButton);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //We have to find a way to pass the level information
                        Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                        intent.putExtra("selectedLevel",selectedLevel);
                        startActivity(intent);
                    }
                });
            }
        }



