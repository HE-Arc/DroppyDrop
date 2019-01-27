package android.hearc.ch.droppydrop.score;
import android.content.Context;
import android.hearc.ch.droppydrop.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    /**
     * Constructor of ScoreManager
     * @param context parent context
     */
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
     * Count the number of scores saved
     * @return number of scores
     */
    public int countScores()
    {
        if(scoresMap.size() > 0)
        {
            int nbScores = 0;
            for (SortedSet ss : scoresMap.values()) {
                nbScores += ss.size();
            }
            return nbScores;
        }
        return 0;
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
                // No file can be written
            }
        }
    }

    /**
     * Save all scores
     */
    public void saveAll(){


        FileOutputStream fos;
        try {
            fos = ScoreManager.context.openFileOutput(ScoreManager.filename, Context.MODE_PRIVATE);
            StringBuilder sb;
            for (Map.Entry<Integer, SortedSet<Score>> entry: scoresMap.entrySet()) {
                sb = new StringBuilder();
                sb.append(entry.getKey()).append("////");
                for (Score score: entry.getValue()) {
                    sb.append(score.value).append("//").append(score.username).append("///");
                }
                sb.append("\n");
                fos.write(sb.toString().getBytes());
            }
            fos.close();
        }
        catch (IOException io) {
        }
    }
}
