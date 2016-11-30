package steep.circular.data;

import java.util.Date;

/**
 * Created by Tom Kretzschmar on 04.11.2016.
 *
 */

public class LightweightEvent {

    private long id;
    private String title;
    private Date date;
    private long cal_id;
    private String cal_title;
    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getCal_id() {
        return cal_id;
    }

    public void setCal_id(long cal_id) {
        this.cal_id = cal_id;
    }

    public String getCal_title() {
        return cal_title;
    }

    public void setCal_title(String cal_title) {
        this.cal_title = cal_title;
    }

    public LightweightEvent(long id, String title, Date date, long cal_id, String cal_title, int color) {
        this.color = color;
        this.id = id;
        this.title = title;
        this.date = date;
        this.cal_id = cal_id;
        this.cal_title = cal_title;
    }
}
