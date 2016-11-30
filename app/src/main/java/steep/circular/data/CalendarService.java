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


    public List<List<LightweightEvent>> getEventPerDayList(MyDate start, MyDate end){

        List<List<LightweightEvent>> events = new ArrayList<>();
        for(int i = 0; i<=366; i++){
            events.add(i, new ArrayList<LightweightEvent>());
        }

        CalendarAdapter calAdpt = new CalendarAdapter(ctx);
        List<Calendar> calendars = calAdpt.queryCalendars();
        if(calendars != null && !calendars.isEmpty()) {
            for (Calendar cal : calendars) {
                Log.d("Iterated Calendars", "Cal: " + cal.getTitle());
//            if(cal.isShowCalendar()) {
                for (Event event : calAdpt.queryEvents(cal, start.getJavaUtilDate(), end.getJavaUtilDate())) {
                    if (event.isReoccuring()) {
                        event.setOccurences(calAdpt.queryOccurences(event, start.getJavaUtilDate(), end.getJavaUtilDate()));
                    }

                    if (event.isReoccuring()) {
                        for (Occurence occurence : event.getOccurences()) { // TODO occurecnces of events, which span multiple days
                            int color = event.getColor() == 0 ? cal.getColor() : event.getColor();
                            LightweightEvent lightEvent = new LightweightEvent(occurence.getId(), event.getTitle(), occurence.getDate(), cal.getId(), cal.getTitle(), color);
                            MyDate d = new MyDate(lightEvent.getDate());
                            events.get(d.getDayOfYear()).add(lightEvent);
                        }
                    } else {
                        int color = event.getColor() == 0 ? cal.getColor() : event.getColor();
                        MyDate s = new MyDate(event.getBegin());
                        MyDate e = new MyDate(event.getEnd());
                        if (s.equals(e)) {
                            LightweightEvent lightEvent = new LightweightEvent(event.getId(), event.getTitle(), event.getBegin(), cal.getId(), cal.getTitle(), color);
                            MyDate d = new MyDate(lightEvent.getDate());
                            events.get(d.getDayOfYear()).add(lightEvent);
                        } else {
                            int diff = e.getDayOfYear() - s.getDayOfYear();
                            for(int k = 0; k<diff; k++){
                                LightweightEvent lightEvent = new LightweightEvent(event.getId(), event.getTitle(), null, cal.getId(), cal.getTitle(), color);

                                events.get(s.getDayOfYear()+k).add(lightEvent);
                            }
                        }
                    }
                }
//            }
            }
        }
        return events;
    }

}
