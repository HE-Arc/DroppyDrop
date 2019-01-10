package android.hearc.ch.droppydrop.game.Level;

import android.content.Context;
import android.hearc.ch.droppydrop.R;

public class LevelModel {

    public int Difficulty;
    public int DropColorInt;
    public int TrackColorInt;

    public String LevelName;
    public int ImageId;

    private Context context;

    public LevelModel(Context context, int levelId) {
        this.context = context;

        this.LevelName = context.getResources().getStringArray(R.array.names)[levelId];
        this.Difficulty = context.getResources().getIntArray(R.array.difficulties)[levelId];
        this.TrackColorInt = context.getResources().obtainTypedArray(R.array.dropColors).getColor(levelId, 0);
        this.DropColorInt = context.getResources().obtainTypedArray(R.array.trackColors).getColor(levelId, 0);
        this.ImageId = context.getResources().obtainTypedArray(R.array.images).getResourceId(levelId, -1);
    }


}