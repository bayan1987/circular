package steep.circular.data;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Tom Kretzschmar on 03.11.2016.
 *
 */

public class CalendarAdapter {

    private Context context;
    private String account_Name = "tkretzschmar94@googlemail.com";
    private String account_Type = "com.google";

    public CalendarAdapter(Context context) {
        this.context = context;
    }

    public List<Calendar> queryCalendars(){
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_COLOR
        };
        String selection = "((" +
                CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" +
                CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{account_Name, account_Type};

        Cursor cursor = query(uri, projection, selection, selectionArgs);
        if(cursor == null) return null;
        List<Calendar> calendars = new ArrayList<>();
        while (cursor.moveToNext()) {
            long calID = cursor.getLong(0);
            String displayName = cursor.getString(1);
            String ownerName = cursor.getString(2);
            int color = cursor.getInt(3);

            Calendar cal = new Calendar(calID, displayName, ownerName, color);
            calendars.add(cal);
            Log.d("available Calendars", displayName + "|" + ownerName + "|" + color);
        }
        cursor.close();
        return calendars;
    }

    public List<Event> queryEvents(Calendar calendar, Date start, Date end){
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.RRULE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_COLOR};


        String selection = "((" +
                CalendarContract.Events.CALENDAR_ID + " = ?) AND (" +
                CalendarContract.Events.DTSTART  + " > ?) AND (" +
                CalendarContract.Events.DTSTART  + " < ?))";



        String i = String.valueOf(calendar.getId());
        String s = String.valueOf(start.getTime());
        String e = String.valueOf(end.getTime());
        String[] selectionArgs = new String[]{i, s, e};

        Cursor cursor = query(uri, projection, selection, selectionArgs);

        List<Event> events = new ArrayList<>();
        while (cursor.moveToNext()){


            Date date = new Date(cursor.getLong(2));

//            Log.d("Cal", "Event: [" + cursor.getString(1) + "] " + date.getDate() + "." + date.getMonth() + "." + date.getYear() + " -> " + cursor.getString(1));

            long eventID = cursor.getLong(0);
            String title = cursor.getString(1);
            boolean reoccuring = (cursor.getString(2) != null);
            Date startEvent = new Date(cursor.getLong(3));
            Date endEvent = new Date(cursor.getLong(4));
            int color = cursor.getInt(5);

            Event event = new Event(eventID, title, reoccuring, startEvent, endEvent, color);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public List<Occurence> queryOccurences(Event event, Date start, Date end){

        String[] projection = {
                CalendarContract.Instances._ID,
                CalendarContract.Instances.BEGIN};

        String selection = CalendarContract.Instances.EVENT_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(event.getId())};

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start.getTime());
        ContentUris.appendId(builder, end.getTime());

        Cursor cursor =  query(builder.build(), projection, selection, selectionArgs);

        List<Occurence> occurences = new ArrayList<>();
        while (cursor.moveToNext()) {
            Date date = new Date(cursor.getLong(1));

//            Log.d("Cal", "Occurence: " + date);
            Occurence occurence = new Occurence(0, date);
            occurences.add(occurence);
        }
        cursor.close();
        return occurences;
    }

    public List<LightweightEvent> queryOccurences(Calendar calendar, Date start, Date end) {

        String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";
        String i = String.valueOf(calendar.getId());
        String[] selectionArgs = new String[]{i};

        String[] projection = {
                CalendarContract.Instances._ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.EVENT_COLOR,
                CalendarContract.Instances.TITLE};

//        String selection = CalendarContract.Instances.EVENT_ID + " = ?";
//        String[] selectionArgs = new String[]{String.valueOf(event.getId())};

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start.getTime());
        ContentUris.appendId(builder, end.getTime());

        Cursor cursor = query(builder.build(), projection, selection, selectionArgs);

        List<LightweightEvent> occurences = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            Date s = new Date(cursor.getLong(1));
            Date e = new Date(cursor.getLong(2));

//            Log.d("Cal", "Occurence: " + date);
            LightweightEvent event = new LightweightEvent(cursor.getLong(0), cursor.getString(4), s, calendar.getId(), calendar.getTitle(), cursor.getInt(3), e);
            Log.i("LightweightEventQuery", event.getId() + "    |    " + event.getCal_title() + "|" + event.getTitle() + "|" + s + "-" + e);


            if(!ids.contains(event.getId())){
                ids.add(event.getId());
            } else {
                Log.e("LightweightEventQuery", "###################### ID " + event.getId() + "|" + event.getTitle() +" ALREADY CONTAINED #########################");
            }

            occurences.add(event);
        }
        cursor.close();

        return occurences;
    }

    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs){
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();

        // Check Permissions
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            return null;
        }

        // Submit the query and get a Cursor object back.
        cursor = cr.query(uri, projection, selection, selectionArgs, null);

        return cursor;
    }
}
