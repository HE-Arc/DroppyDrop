package android.hearc.ch.droppydrop.score;

public class Score {

    private int value;
    private String username;

    public Score(int value, String username)
    {
        this.value = value;
        this.username = username;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(username).append("\t");
        builder.append(value).append(" pts").append("\t");
        return  builder.toString();
    }
}
