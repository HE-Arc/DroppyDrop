package android.hearc.ch.droppydrop.game.Level;

import android.content.Context;
import android.content.res.TypedArray;
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

    public LevelModel(Context context, int levelId)
    {
        this.context=context;
        //String[] array =
        //Log.i(TAG, "LevelModel: " + obj.getClass().getSimpleName());
        //this.id = context.getResources().obtainTypedArray(reference).getResources();


        int firstLevelAdress=0x7f020000; // dsl mdr


        this.LevelName = context.getResources().obtainTypedArray(firstLevelAdress+levelId).getString(0);
        this.ImageId= context.getResources().obtainTypedArray(firstLevelAdress+levelId).getResourceId(1,0);
        this.Difficulty=context.getResources().obtainTypedArray(firstLevelAdress+levelId).getInteger(2,0);
        this.DropColorInt=context.getResources().obtainTypedArray(firstLevelAdress+levelId).getColor(3,0);
        this.TrackerColorInt=context.getResources().obtainTypedArray(firstLevelAdress+levelId).getColor(4,0);

    }

    public static List<LevelModel> getAllLevelModel(Context context)
    {

        List<LevelModel> models = new ArrayList<>();

        int levelCount = context.getResources().getStringArray(R.array).length;
        for(int i=0;i<levelCount;i++)
        {
            models.add(new LevelModel(context, i));
        }

        return  models;
    }
}
