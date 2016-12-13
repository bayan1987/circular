package steep.circular.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import steep.circular.data.Calendar;
import steep.circular.data.CalendarAdapter;
import steep.circular.data.Event;
import steep.circular.data.MyDate;

/**
 * Created by Tom Kretzschmar on 04.11.2016.
 *
 */

public class CalendarService{
    private Context ctx;

    public CalendarService(Context ctx) {
        this.ctx = ctx;
    }

    public List<List<Event>> getEventPerDayList(MyDate start, MyDate end) {
        List<List<Event>> events = new ArrayList<>();
        for (int i = 0; i <= 366; i++) {
            events.add(i, new ArrayList<Event>());
        }
        CalendarAdapter calAdpt = new CalendarAdapter(ctx);
        List<Calendar> calendars = calAdpt.queryCalendars();
        if (calendars != null && !calendars.isEmpty()) {
            for (Calendar cal : calendars) {
                Log.d("Iterated Calendars", "Cal: " + cal.getTitle());
                cal.setShowCalendar(true); // TODO: Calendar Selection
                if (cal.isShowCalendar()) {
                    for (Event event : calAdpt.queryOccurences(cal, start.getJavaUtilDate(), end.getJavaUtilDate())) {
                        int color = event.getColor() == 0 ? cal.getColor() : event.getColor();
                        MyDate s = new MyDate(event.getDate());
                        MyDate e = new MyDate(event.getEnd());
                        if (s.equals(e)) {
                            Event lightEvent = new Event(event.getId(), event.getTitle(), event.getDate(), cal.getId(), cal.getTitle(), color);
                            MyDate d = new MyDate(lightEvent.getDate());
                            events.get(d.getDayOfYear()).add(lightEvent);
                        } else {
                            int diff = e.getDayOfYear() - s.getDayOfYear();
                            for (int k = 0; k < diff; k++) {
                                Event lightEvent = new Event(event.getId(), event.getTitle(), null, cal.getId(), cal.getTitle(), color);

                                events.get(s.getDayOfYear() + k).add(lightEvent); // TODO: split list in different calendars to provide proper sorting of events
                            }
                        }
                    }
                }
            }
        }
        return events;
    }

}
