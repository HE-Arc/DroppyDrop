package android.hearc.ch.droppydrop.score;
import android.content.Context;
import android.hearc.ch.droppydrop.R;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ScoreManager Singleton
 */
public class ScoreManager {
    private static ScoreManager instance;
    private static Context context;

    private Map<Integer, List<Score>> scoresMap;

    private ScoreManager(Context context)
    {
        ScoreManager.context = context;
        this.scoresMap = new TreeMap<>();
        readScores();
    }

    /**
     * Get instance of ScoreManager
     * @param context context of the app
     * @return ScoreManager instance
     */
    public static ScoreManager getInstance(Context context)
    {
        if(instance == null) {
            instance = new ScoreManager(context);
        }

        return instance;
    }

    /**
     *  Save a score in the file system
     * @param score
     */
    public void saveScore(Score score)
    {
        Log.i("SCORE", "saveScore");
        List<Score> levelScores = scoresMap.get(score.level);
        if(levelScores != null) {
            for (int i = 0; i < levelScores.size(); i++) {
                Score list_score = levelScores.get(i);
                if (list_score.value < score.value)
                    levelScores.add(0, score);
            }
            if(levelScores.size() > 3) {    // if we have more than 3 scores stored
                levelScores.remove(3);
            }
        } else {
            levelScores = new ArrayList<>();
            levelScores.add(score);
            scoresMap.put(score.level, levelScores);
        }
        saveAll();  // can be optimized if we only change the value of the line
    }

    /**
     * Get the best scores for a level
     * @param level
     * @return ArrayList<Score> scores
     */
    public List<Score> getScoresByLevel(int level)
    {
        return scoresMap.get(level);    // Return the array of the 3 best scores
    }

    /**
     * Get the best scores of all level
     * @return Map<Integer, List<Score>>
     */
    public Map<Integer, List<Score>> getAllScores()
    {
        return scoresMap;
    }

    /**
     * Read the scores and store them in the instance
     */
    private void readScores()
    {
        FileInputStream fis;
        try{
            fis = ScoreManager.context.openFileInput(ScoreManager.context.getResources().getString(R.string.score_file));

            byte[] buffer = new byte[1024];
            StringBuffer fileContent = new StringBuffer();
            int n;
            while ((n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
            fis.close();
            String content = fileContent.toString();
            String[] lines = content.split("\n");
            Log.i("SCORE", "readScores: " + content);

            for(String line : lines)
            {
                // TODO Validation regex
                String[] levelLine = line.split("////");
                Integer lvl = Integer.getInteger(levelLine[0]);
                List<Score> scores = new LinkedList<>();
                if(levelLine.length > 1) {  // If scores have already been registerd
                    String[] strScores = levelLine[1].split("///");
                    for (String strScore : strScores) {
                        if (strScore.length() > 1)    // TODO validation regex
                            scores.add(Score.fromLine(lvl, strScore));
                        // else endline
                    }
                }
                scoresMap.put(lvl, scores);
            }
        } catch (IOException e) {
            FileOutputStream fos;
            try {
                fos = ScoreManager.context.openFileOutput(ScoreManager.context.getResources().getString(R.string.score_file), Context.MODE_PRIVATE);
                int levelCount = ScoreManager.context.getResources().getStringArray(R.array.names).length;
                StringBuilder content = new StringBuilder();
                for (int i = 1; i <= levelCount; i++) {
                    content.append(i + "////\n"); // only save the level id
                }
                fos.write(content.toString().getBytes());
            }
            catch (IOException io)
            {
                // Not file can be written
                Log.i("Score", "readAll: " + io.toString());
            }
        }
    }

    /**
     * Save all scores
     */
    public void saveAll(){
        Log.i("SCORE","saveAll");

        FileOutputStream fos;
        try {
            fos = ScoreManager.context.openFileOutput(ScoreManager.context.getResources().getString(R.string.score_file), Context.MODE_PRIVATE);
            Log.i("SCORE", "saveAll: fos opened");
            StringBuilder sb;
            for (Map.Entry<Integer, List<Score>> entry: scoresMap.entrySet()) {
                sb = new StringBuilder();
                sb.append(entry.getKey()).append("////");
                for (Score score: entry.getValue()) {
                    sb.append(score.value).append("//").append(score.username).append("///");
                }
                sb.append("\n");
                fos.write(sb.toString().getBytes());
            }
            fos.close();
            Log.i("SCORE", "saveAll: fos closed");
        }
        catch (IOException io) {
            Log.i("Score", "saveAll: " + io.toString());
        }
    }

    /**
     * Verify if the external storage is writable
     * @return boolean
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Verify if the external storage is readable
     * @return boolean
     */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
