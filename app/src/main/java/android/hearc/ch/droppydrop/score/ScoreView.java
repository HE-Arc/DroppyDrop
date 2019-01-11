package android.hearc.ch.droppydrop.score;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Score View
 */
public class ScoreView extends LinearLayout {
    private Score score;

    private TextView textView;

    public ScoreView(Context context, Score score) {
        super(context);
        this.score = score;


    }

    private void init() {
        //
    }
}
