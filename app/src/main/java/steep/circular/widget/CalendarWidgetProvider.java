package steep.circular.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

import steep.circular.R;
import steep.circular.activities.MainActivity;
import steep.circular.data.Event;
import steep.circular.data.MyDate;
import steep.circular.service.CalendarService;
import steep.circular.view.CircleCalendarView;

/**
 * Created by Tom Kretzschmar on 03.01.2017.
 *
 */

public class CalendarWidgetProvider extends AppWidgetProvider{


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);



            CircleCalendarView calendarView = new CircleCalendarView(context);

            MyDate start = MyDate.getToday();
            MyDate end = MyDate.getToday();
            end.setYear(end.getYear() + 1);

            CalendarService calSrv = new CalendarService(context);
            List<List<Event>> list = calSrv.getEventPerDayList(start, end);
            calendarView.setEvents(list);


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);

//            calendarView.measure(920, 920);
            calendarView.layout(0, 0, 900, 900);
            Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            calendarView.draw(new Canvas(bitmap));

            views.setImageViewBitmap(R.id.imageView, bitmap);

            views.setOnClickPendingIntent(R.id.imageView, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

            Log.d("CalendarWidgetProvider", "onUpdate()");
        }
    }
}
