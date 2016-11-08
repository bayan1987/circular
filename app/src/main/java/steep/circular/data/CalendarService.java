package steep.circular.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tom Kretzschmar on 04.11.2016.
 *
 */

public class CalendarService{
    private Context ctx;

    public CalendarService(Context ctx) {
        this.ctx = ctx;
    }

    public List<Calendar> getAllEvents(Date start, Date end){
        CalendarAdapter calAdpt = new CalendarAdapter(ctx);
        List<Calendar> calendars = calAdpt.queryCalendars();
        if(calendars != null && !calendars.isEmpty()) {
            for (Calendar cal : calendars) {
//            if(cal.isShowCalendar()) {
                cal.setEvents(calAdpt.queryEvents(cal, start, end));
                for (Event event : cal.getEvents()) {
                    if (event.isReoccuring()) {
                        event.setOccurences(calAdpt.queryOccurences(event, start, end));
                    }
                }
//            }
            }
        }
        return calendars;
    }

    public List<LightweightEvent> getLightweightEvents(Date start, Date end){

        List<LightweightEvent> lightEvents = new ArrayList<>();

        CalendarAdapter calAdpt = new CalendarAdapter(ctx);
        List<Calendar> calendars = calAdpt.queryCalendars();
        if(calendars != null && !calendars.isEmpty()) {
            for (Calendar cal : calendars) {
//            if(cal.isShowCalendar()) {
                for (Event event : calAdpt.queryEvents(cal, start, end)) {
                    if (event.isReoccuring()) {
                        event.setOccurences(calAdpt.queryOccurences(event, start, end));
                    }

                    if (event.isReoccuring()) {
                        for (Occurence occurence : event.getOccurences()) {
                            LightweightEvent lightEvent = new LightweightEvent(occurence.getId(), event.getTitle(), occurence.getDate(), cal.getId(), cal.getTitle());
                            lightEvents.add(lightEvent);
                        }
                    } else {
                        LightweightEvent lightEvent = new LightweightEvent(event.getId(), event.getTitle(), event.getBegin(), cal.getId(), cal.getTitle());
                        lightEvents.add(lightEvent);
                    }
                }
//            }
            }
        }
        return lightEvents;
    }
}
