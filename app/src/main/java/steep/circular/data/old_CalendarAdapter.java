package steep.circular.data;

/**
 * Created by Tom Kretzschmar on 21.10.2016.
 *
 */

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.EventDays;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.provider.CalendarContract.Instances;

import java.util.Calendar;

public class old_CalendarAdapter {

    private Context context;

    public old_CalendarAdapter(Context context) {
        this.context = context;
    }

    // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;



    public boolean query(final String account_Name, final String account_Type, final String owner_Account) {
        // Query 1 - Calendars
        Log.d("Cal", "query");
        // Run query
        Cursor cur;
        ContentResolver cr = context.getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String selection;
        String[] selectionArgs;
        if (owner_Account != null) {
            selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" + Calendars.ACCOUNT_TYPE + " = ?) AND (" + Calendars.OWNER_ACCOUNT + " = ?))";
            selectionArgs = new String[]{account_Name, account_Type, owner_Account};
        } else {
            selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" + Calendars.ACCOUNT_TYPE + " = ?))";
            selectionArgs = new String[]{account_Name, account_Type};
        }


        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.d("Cal", displayName + "|" + accountName + "|" + ownerName);

        }

        // Query 2 - Events
        Log.d("Cal", "query 2");
        Uri uri2 = Events.CONTENT_URI;
        String[] projection = {Events.TITLE, Events.DTSTART, Events.RRULE, Events.RDATE, Events._ID};
        Cursor cur2;

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2016, 9, 2);
        String selection2 = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" + Calendars.ACCOUNT_TYPE + " = ?) AND (" + Events.DTSTART  + " > ?))";
        selectionArgs = new String[]{account_Name, account_Type, String.valueOf(cal2.getTimeInMillis())};

        cur2 = cr.query(uri2, projection, selection2, selectionArgs, Events.DTSTART);


        int i = 0;
        while (cur2.moveToNext()){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cur2.getLong(1));

            Log.d("Cal", "Event: [" + cur2.getString(4) + "] " + cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR) + " -> " + cur2.getString(0) + " ||| Rule: " + cur2.getString(2) + " | " + cur2.getString(3));
            i++;

            if(i == 100) break;
        }

        // Query 3 Calendar Instances - Reoccuring Event
        Log.d("Cal", "query 2");
        Uri uri3 = Events.CONTENT_URI;
        String[] projection3 = {Instances.TITLE, Instances.BEGIN};
        Cursor cur3;

        Calendar begin = Calendar.getInstance();
        begin.set(2016, 9, 2);
        Calendar end = Calendar.getInstance();
        end.set(2016, 12, 2);

        String selection3 = CalendarContract.Instances.EVENT_ID + " = ?";
        selectionArgs = new String[]{"466"};

        // Construct the query with the desired date range.
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, begin.getTimeInMillis());
        ContentUris.appendId(builder, end.getTimeInMillis());

        // Submit the query
        cur3 =  cr.query(builder.build(), projection3, selection3, selectionArgs, null);

        int j = 0;
        while (cur3.moveToNext()){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cur3.getLong(1));

            Log.d("Cal", "Reoccuring Event: " + cur3.getString(0) + " | "  + cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR));
            j++;
            if(j == 100) break;
        }

        return true;
    }
}
