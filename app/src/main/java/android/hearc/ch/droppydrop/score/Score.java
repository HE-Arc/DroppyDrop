package android.hearc.ch.droppydrop.score;

public class Score {

    public int value;
    public String username;
    public int level;

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
    public static Score fromLine(int level, String line)
    {
        String[] content = line.split("//");
        return new Score(level, Integer.getInteger(content[0]), content[1]);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(level).append("\t");
        builder.append(username).append("\t");
        builder.append(value).append(" pts").append("\t");
        return  builder.toString();
    }
}
