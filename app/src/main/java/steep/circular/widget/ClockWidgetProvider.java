package steep.circular.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import steep.circular.R;
import steep.circular.view.CircleClockView;

/**
 * Created by Tom Kretzschmar on 22.09.2016.
 *
 */

public class ClockWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CLOCK_UPDATE = "CLOCK_UPDATE";

    private AppWidgetAlarm alarm;

    private int countWidgets = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(ACTION_CLOCK_UPDATE))
        {
            Log.d("ClockWidgetProvider", "onReceive " + intent);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
            ComponentName componentName = new ComponentName(context, ClockWidgetProvider.class);

            CircleClockView circleClockView = new CircleClockView(context);
//            CircleClockView.STROKE_WIDTH_DP = 5;
            int hour = Calendar.getInstance().get(Calendar.HOUR);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            circleClockView.updateWidgetClock(hour, minute);
            circleClockView.measure(900, 900);
            circleClockView.layout(0, 0, 900, 900);
            Bitmap bitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
            circleClockView.draw(new Canvas(bitmap));

            views.setImageViewBitmap(R.id.imageView, bitmap);

            AppWidgetManager.getInstance(context).updateAppWidget(componentName, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        countWidgets ++;

        alarm = new AppWidgetAlarm(context.getApplicationContext());
        alarm.startAlarm();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        // TODO: alarm should be stopped only if all widgets has been disabled

        countWidgets --;

        // stop alarm
        if(alarm != null && countWidgets == 0) {
            alarm.stopAlarm();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);

            CircleClockView circleClockView = new CircleClockView(context);
//            CircleClockView.STROKE_WIDTH_DP = 5;
            int hour = Calendar.getInstance().get(Calendar.HOUR);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            circleClockView.updateWidgetClock(hour, minute);
            circleClockView.measure(900, 900);
            circleClockView.layout(0, 0, 900, 900);
            Bitmap bitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
            circleClockView.draw(new Canvas(bitmap));

            views.setImageViewBitmap(R.id.imageView, bitmap);

            appWidgetManager.updateAppWidget(appWidgetId, views);

            Log.d("ClockWidgetProvider", "onUpdate()");
        }
    }
}
