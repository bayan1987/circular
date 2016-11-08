package steep.circular.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom Kretzschmar on 03.11.2016.
 *
 */

public class Calendar {

    private long id;
    private String title;
    private String owner;
    private String color;
    private boolean showCalendar;

    private List<Event> events;


    public Calendar(long id, String title, String owner, String color) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.color = color;

        events = new ArrayList<>();
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isShowCalendar() {
        return showCalendar;
    }

    public void setShowCalendar(boolean showCalendar) {
        this.showCalendar = showCalendar;
    }

    public void addEvent(Event event){
        events.add(event);
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents(){
        return events;
    }
}
