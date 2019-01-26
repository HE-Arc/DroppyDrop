package android.hearc.ch.droppydrop.game.Level;

import android.content.Context;
import android.hearc.ch.droppydrop.R;

/**
 * Class of Level Model
 */
public class LevelModel {

    public int levelId;
    public int ImageId;
    public int Difficulty;
    public int DropColorInt;
    public int TrackColorInt;
    public String LevelName;

    private Context context;

    /**
     * Constructor of level model
     * @param context parent contxt of the level
     * @param levelId level id
     */
    public LevelModel(Context context, int levelId) {
        this.context = context;
        this.levelId=levelId;

        // get the xml attributes corresponding to the levelId
        this.ImageId = context.getResources().obtainTypedArray(R.array.images).getResourceId(levelId, -1);
        this.Difficulty = context.getResources().getIntArray(R.array.difficulties)[levelId];
        this.DropColorInt = context.getResources().obtainTypedArray(R.array.trackColors).getColor(levelId, 0);
        this.TrackColorInt = context.getResources().obtainTypedArray(R.array.dropColors).getColor(levelId, 0);
        this.LevelName = context.getResources().getStringArray(R.array.names)[levelId];
    }
}