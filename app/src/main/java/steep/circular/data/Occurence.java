package steep.circular.data;

import java.util.Date;

/**
 * Created by Tom Kretzschmar on 03.11.2016.
 *
 */
public class Occurence {
    private int id;
    private Date date;

    public Occurence(int id, Date date) {
        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
