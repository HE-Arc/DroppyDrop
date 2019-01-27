package android.hearc.ch.droppydrop.score;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

/**
 * Score
 */
public class Score implements Comparable<Score> {
    public int value;
    public String username;
    public int level;

    /**
     * Constructor of Score
     * @param level level of the score
     * @param value points scored
     * @param username username of whom performed it
     */
    public Score(int level, int value, String username)
    {
        this.level = level;
        this.value = value;
        this.username = username;
    }

    /**
     * Build a score from a string line
     * @param line like "intscore//username"
     */
    public static Score parse(int level, String line)
    {
        String[] content = line.split("//");
        return new Score(level, Integer.parseInt(content[0]), content[1]);
    }

    /**
     * To String Function
     * @return score as a string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(level).append("\t");
        builder.append(username).append("\t");
        builder.append(value).append(" pts").append("\t");
        return  builder.toString();
    }

    /**
     * CompareTo another score considering the value attribute
     * @param other_score score compared to
     * @return 1 if bigger, 0 if same and -1 if smaller
     */
    @Override
    public int compareTo(@NonNull Score other_score) {
        return Integer.compare(value, other_score.value);
    }
}
