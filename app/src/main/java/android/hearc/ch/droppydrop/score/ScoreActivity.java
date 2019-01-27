package android.hearc.ch.droppydrop.score;

import android.hearc.ch.droppydrop.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.util.Log;
import android.widget.TextView;

/**
 * ScoreActivity
 */
public class ScoreActivity extends AppCompatActivity {
    private static final String TAG = ScoreActivity.class.getSimpleName();

    private ExpandableListView scoreListView;
    private ScoreExpandableAdapter scoreExpandableAdapter;
    private ScoreManager scoreManager;

    /**
     * OnCreate set content view and set up views
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        this.scoreManager = ScoreManager.getInstance(this);

        retrieveView();
        setUpViews();

        // cup icon from : http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/128/cup-icon.png
    }

    /**
     * Retrive Views from resources
     */
    private void retrieveView() {
        scoreListView = findViewById(R.id.scoreListView);
    }

    /**
     * Set up view
     */
    private void setUpViews() {
        Log.d(TAG, "Setting up views...");

        // addFalseScore(); // False scores to test
        if(scoreManager.countScores() > 0) {    // if scores are saved
            // Show with expandable listView
            scoreExpandableAdapter = new ScoreExpandableAdapter(this, scoreManager);
            scoreListView.setAdapter(scoreExpandableAdapter);
        }
        else {
            // Show No Score label
            TextView noScoreTV = findViewById(R.id.noScoreTV);
            noScoreTV.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "Setting up views -> done");
    }

    /**
     * Add five scores for level 1
     */
    private void addFalseScore()
    {
        scoreManager.saveScore(new Score(1, 2000, "Joueur 1"));
        Log.i(TAG, "addFalseScore: 1 ok");
        scoreManager.saveScore(new Score(1, 2300, "Joueur 2"));
        Log.i(TAG, "addFalseScore: 2 ok");
        scoreManager.saveScore(new Score(1, 4500, "Joueur 3"));
        Log.i(TAG, "addFalseScore: 3 ok");
        scoreManager.saveScore(new Score(1, 1290, "Joueur 4"));
        Log.i(TAG, "addFalseScore: 4 ok");
        scoreManager.saveScore(new Score(1, 999, "Joueur 5"));
        Log.i(TAG, "addFalseScore: 5 ok");
    }
}
