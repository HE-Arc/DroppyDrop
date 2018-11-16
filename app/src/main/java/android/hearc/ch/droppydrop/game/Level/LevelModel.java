package android.hearc.ch.droppydrop.game.Level;

import android.content.Context;
import android.hearc.ch.droppydrop.R;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LevelModel {

    public String LevelName;
    public int id;
    public int ImageId;
    public int Difficulty;
    public int DropColorInt;
    public int TrackerColorInt;


    private Context context;

    public LevelModel(Context context, int reference)
    {
        this.context=context;
        //String[] array =
        //Log.i(TAG, "LevelModel: " + obj.getClass().getSimpleName());
        //this.id = context.getResources().obtainTypedArray(reference).getResources();
        //this.LevelName = context.getResources().getStringArray(R.array.names)[levelId];
        this.ImageId= context.getResources().obtainTypedArray(reference).getResourceId(image,-1);
        this.Difficulty=context.getResources().getIntArray(R.array.difficulties)[levelId];
        this.DropColorInt=context.getResources().obtainTypedArray(R.array.dropColors).getColor(levelId,0);

    }

    public static List<LevelModel> getAllLevelModel(Context context)
    {
        List<LevelModel> models = new ArrayList<>();
        models.add(new LevelModel(context, R.array.level_1));
        return  null;
    }
}
