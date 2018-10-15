package android.hearc.ch.droppydrop;

import java.util.Date;

public class Score {

    private int value;
    private String username;
    private Date date;

    public Score(int value, String username, Date date)
    {
        this.value = value;
        this.username = username;
        this.date = (Date) date.clone();
    }
}
