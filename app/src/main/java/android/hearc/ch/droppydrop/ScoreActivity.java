package android.hearc.ch.droppydrop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.util.Log;

public class ScoreActivity extends AppCompatActivity {

    private static final String TAG = ScoreActivity.class.getSimpleName();

    private ListView scoreListView;
    private ScoreAdapter scoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

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

        loadBestScore();

        Log.d(TAG, "Setting up views -> done");
    }

    private void loadBestScore() {
        scoreAdapter.add("Ola");
        scoreAdapter.add("Hello");
        scoreAdapter.add("Ciao");
        scoreAdapter.add("Hallo");
        scoreAdapter.add("Si amiga");
    }
}
