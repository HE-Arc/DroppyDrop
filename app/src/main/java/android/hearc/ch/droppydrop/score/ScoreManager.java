package android.hearc.ch.droppydrop.score;
import android.content.Context;
import android.hearc.ch.droppydrop.R;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * ScoreManager Singleton
 */
public class ScoreManager {
    private final static String TAG = "SCORE";

    private static ScoreManager instance;
    private static Context context;
    private static String filename;

    private Map<Integer, SortedSet<Score>> scoresMap;

    private ScoreManager(Context context)
    {
        ScoreManager.context = context;
        ScoreManager.filename = ScoreManager.context.getResources().getString(R.string.score_file);
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
        Log.i(TAG, "saveScore");
        SortedSet<Score> levelScores = scoresMap.get(score.level);
        if(levelScores != null && levelScores.size() >= 1) {
            levelScores.add(score);
            if(levelScores.size() > 3) {    // if we have more than 3 scores stored
                levelScores.remove(levelScores.last());
            }
        } else {
            levelScores = new TreeSet<>();
            levelScores.add(score);
            scoresMap.put(score.level, levelScores);
        }
        saveAll();  // can be optimized if we only change the value of the line
    }

    /**
     * Get the best scores for a level
     * @param level
     * @return SortedSet<Score> scores
     */
    public SortedSet<Score> getScoresByLevel(int level)
    {
        return scoresMap.get(level);    // Return the sortedset of the 3 best scores
    }

    /**
     * Get the best scores of all level
     * @return Map<Integer, List<Score>>
     */
    public Map<Integer, SortedSet<Score>> getAllScores()
    {
        return scoresMap;
    }

    /**
     * Get a specific score of a level and a rank
     * @param level level number [1, nbLevel]
     * @param rank [1, 3]
     * @return Score or null
     */
    public Score getSpecificScore(int level, int rank)
    {
        SortedSet<Score> scoreSet = scoresMap.get(level);
        if(scoreSet != null){
            Iterator<Score> it = scoreSet.iterator();
            Score score;
            int i = 1;
            while (it.hasNext())
            {
                score = it.next();
                if (i == rank)
                    return score;
                i++;
            }
        }
        return null;
    }

    /**
     * Read the scores and store them in the instance
     */
    private void readScores()
    {
        FileOutputStream fos;
        FileInputStream fis;
        try{
            fis = ScoreManager.context.openFileInput(ScoreManager.filename);

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
            Log.i(TAG, "readScores: " + content);

            for(String line : lines)
            {
                // Get level ID of the line
                String[] levelLine = line.split("////");
                Integer lvl = Integer.parseInt(levelLine[0]);

                SortedSet<Score> scores = new TreeSet<>();
                if(levelLine.length > 1) {  // If scores have already been registerd
                    // Separate best scores
                    String[] strScores = levelLine[1].split("///");
                    for (String strScore : strScores) { // foreach scores registered
                        if (strScore.length() > 1)
                            scores.add(Score.parse(lvl, strScore));
                        // else endline
                    }
                }
                scoresMap.put(lvl, scores);
            }
            Log.i(TAG, "readScores: has read all correctly");
        } catch (IOException e) {
            try {
                fos = ScoreManager.context.openFileOutput(ScoreManager.filename, Context.MODE_PRIVATE);
                int levelCount = ScoreManager.context.getResources().getStringArray(R.array.names).length;
                StringBuilder content = new StringBuilder();
                for (int i = 1; i <= levelCount; i++) {
                    content.append(i + "////\n"); // only save the level id
                }
                fos.write(content.toString().getBytes());
                fos.close();
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

        Log.i(TAG, "saveAll: " + ScoreManager.filename);

        FileOutputStream fos;
        try {
            fos = ScoreManager.context.openFileOutput(ScoreManager.filename, Context.MODE_PRIVATE);
            Log.i(TAG, "saveAll: fos opened");
            StringBuilder sb;
            for (Map.Entry<Integer, SortedSet<Score>> entry: scoresMap.entrySet()) {
                sb = new StringBuilder();
                sb.append(entry.getKey()).append("////");
                for (Score score: entry.getValue()) {
                    sb.append(score.value).append("//").append(score.username).append("///");
                }
                sb.append("\n");
                fos.write(sb.toString().getBytes());
                Log.i(TAG, "saveAll: fos has write");
            }
            fos.close();
            Log.i(TAG, "saveAll: fos closed");
        }
        catch (IOException io) {
            Log.i(TAG, "saveAll: " + io.toString());
        }
    }
}
