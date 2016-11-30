package steep.circular.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tom Kretzschmar on 03.11.2016.
 *
 */
public class Event {
    private long id;
    private String title;
    private boolean reoccuring;
    private Date begin;
    private Date end;
    private int color;

    private List<Occurence> occurences;

    public Event(long id, String title, boolean reoccuring, Date begin, Date end, int color) {
        this.id = id;
        this.title = title;
        this.reoccuring = reoccuring;
        this.begin = begin;
        this.end = end;
        this.color = color;

        occurences = new ArrayList<>();
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

    public boolean isReoccuring() {
        return reoccuring;
    }

    public void setReoccuring(boolean reoccuring) {
        this.reoccuring = reoccuring;
    }

    public void setOccurences(List<Occurence> occurences) {
        this.occurences = occurences;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Occurence> getOccurences() {
        return occurences;
    }

    public void addOccurence(Occurence occurence){
        occurences.add(occurence);
    }
}
