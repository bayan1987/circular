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
    // TODO: account name & type from user / no input
    private static final String ACCOUNT_NAME = "tkretzschmar94@googlemail.com";
//    private static final String ACCOUNT_NAME = "m.zwinzscher@googlemail.com";
    private static final String ACCOUNT_TYPE = "com.google";

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
        String[] selectionArgs = new String[]{ACCOUNT_NAME, ACCOUNT_TYPE};

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

    public List<Event> queryOccurences(Calendar calendar, Date start, Date end) {
        String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";
        String i = String.valueOf(calendar.getId());
        String[] selectionArgs = new String[]{i};

        String[] projection = {
                CalendarContract.Instances._ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.EVENT_COLOR,
                CalendarContract.Instances.TITLE};

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start.getTime());
        ContentUris.appendId(builder, end.getTime());

        Cursor cursor = query(builder.build(), projection, selection, selectionArgs);

        List<Event> occurences = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        while (cursor.moveToNext()) {
            Date s = new Date(cursor.getLong(1));
            Date e = new Date(cursor.getLong(2));
            Event event = new Event(cursor.getLong(0), cursor.getString(4), s, calendar.getId(), calendar.getTitle(), cursor.getInt(3), e);
            occurences.add(event);
        }
        cursor.close();

        return occurences;
    }

    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs){
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();

        // TODO Check and ask for calendar read permission
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            return null;
        }

        // Submit the query and get a Cursor object back.
        cursor = cr.query(uri, projection, selection, selectionArgs, null);

        return cursor;
    }
}
