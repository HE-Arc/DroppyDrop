package android.hearc.ch.droppydrop.score;

import android.hearc.ch.droppydrop.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class ScoreActivity extends AppCompatActivity {

    private static final String TAG = ScoreActivity.class.getSimpleName();

    private ListView scoreListView;
    private ScoreAdapter scoreAdapter;
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        this.scoreManager = ScoreManager.getInstance(this);

        retriveView();
        setUpViews();

        // cup icon from : http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/128/cup-icon.png
    }

    private void retriveView() {
        Log.i(TAG, "Retrieving views...");

        scoreListView = findViewById(R.id.scoreListView);

        Log.i(TAG, "Retrieving views -> done");
    }

    private void setUpViews() {
        Log.d(TAG, "Setting up views...");

        scoreAdapter = new ScoreAdapter(this);
        scoreListView.setAdapter(scoreAdapter);
        addFalseScore();
        loadBestScore();

        Log.d(TAG, "Setting up views -> done");
    }

    private void addFalseScore()
    {
        scoreManager.saveScore(new Score(1, 2000, "Joueur 1"));
        scoreManager.saveScore(new Score(1, 2300, "Joueur 2"));
        scoreManager.saveScore(new Score(1, 4500, "Joueur 3"));
        scoreManager.saveScore(new Score(1, 1290, "Joueur 4"));
        scoreManager.saveScore(new Score(1, 999, "Joueur 5"));

    }

    private void loadBestScore() {

        for (Map.Entry<Integer, List<Score>> entry : scoreManager.getAllScores().entrySet())
        {
            for(Score score : entry.getValue())
            {
                scoreAdapter.add(score.toString());
            }
        }
    }
}
