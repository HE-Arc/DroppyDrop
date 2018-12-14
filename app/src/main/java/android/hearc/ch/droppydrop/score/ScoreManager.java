package android.hearc.ch.droppydrop.score;
import android.content.Context;
import android.hearc.ch.droppydrop.R;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        List<Score> levelScores = scoresMap.get(score.level);
        if(levelScores.size() > 0) {
            for (int i = 0; i < levelScores.size(); i++) {
                Score list_score = levelScores.get(i);
                if (list_score.value < score.value)
                    levelScores.add(0, score);
            }
            if(levelScores.size() > 3) {    // if we have more than 3 scores stored
                levelScores.remove(3);
            }
        } else {
            levelScores.add(score);
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
     * Read the scores and store them in the instance
     */
    private void readScores()
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File scoreFile = new File(sdcard, ScoreManager.context.getResources().getString(R.string.score_file));

        try{
            if(!isExternalStorageReadable())
            {
                throw new IOException();
            }
            BufferedReader br = new BufferedReader(new FileReader(scoreFile));
            String line;
            while ((line = br.readLine()) != null)
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
            br.close();
        } catch (IOException e) {
            if(isExternalStorageWritable())
            {
                try {
                    FileWriter fw = new FileWriter(scoreFile, false);
                    int levelCount = ScoreManager.context.getResources().getStringArray(R.array.names).length;
                    for (int i = 1; i <= levelCount; i++) {
                        fw.write(i + "////\n"); // only save the level id
                    }
                }
                catch (IOException io)
                {
                    // Not file can be written
                }
            }
        }
    }

    /**
     * Save all scores
     */
    public void saveAll(){
        File sdcard = Environment.getExternalStorageDirectory();
        File scoreFile = new File(sdcard, ScoreManager.context.getResources().getString(R.string.score_file));

        if(isExternalStorageWritable()) {
            try {
                FileWriter fw = new FileWriter(scoreFile, false);
                StringBuilder sb;
                for (Map.Entry<Integer, List<Score>> entry: scoresMap.entrySet()) {
                    sb = new StringBuilder();
                    sb.append(entry.getKey()).append("////");
                    for (Score score: entry.getValue()) {
                        sb.append(score.value).append("//").append(score.username).append("///");
                    }
                    sb.append("\n");
                    fw.write(sb.toString());
                }
            }
            catch (IOException io) {
                // No file can be written
            }
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
