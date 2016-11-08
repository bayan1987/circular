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
 * Created by Tom on 22.09.2016.
 */

public class ClockWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CLOCK_UPDATE = "CLOCK_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(ACTION_CLOCK_UPDATE))
        {
            Log.d("ClockWidgetProvider", "onReceive " + intent);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
            ComponentName componentName = new ComponentName(context, ClockWidgetProvider.class);




            CircleClockView circleClockView = new CircleClockView(context);
            int minute = Calendar.getInstance().get(Calendar.HOUR);
            int second = Calendar.getInstance().get(Calendar.MINUTE);
            int millisecond = Calendar.getInstance().get(Calendar.SECOND);
            circleClockView.updateWidgetClock(minute, second, millisecond);
            circleClockView.measure(1000, 1000);
            circleClockView.layout(0, 0, 1000, 1000);
            Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            circleClockView.draw(new Canvas(bitmap));

            views.setImageViewBitmap(R.id.imageView, bitmap);

            AppWidgetManager.getInstance(context).updateAppWidget(componentName, views);

//            Log.d("ClockWidgetProvider", "onUpdate()");

        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
        Log.d("ClockWidgetProvider", "onEnabled()");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        // TODO: alarm should be stopped only if all widgets has been disabled

        // stop alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.stopAlarm();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);

            CircleClockView circleClockView = new CircleClockView(context);
            int minute = Calendar.getInstance().get(Calendar.HOUR);
            int second = Calendar.getInstance().get(Calendar.MINUTE);
            int millisecond = Calendar.getInstance().get(Calendar.SECOND);
            circleClockView.updateWidgetClock(minute, second, millisecond);
            circleClockView.measure(1000, 1000);
            circleClockView.layout(0, 0, 1000, 1000);
            Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            circleClockView.draw(new Canvas(bitmap));

            views.setImageViewBitmap(R.id.imageView, bitmap);

            appWidgetManager.updateAppWidget(appWidgetId, views);

            Log.d("ClockWidgetProvider", "onUpdate()");
        }
    }
}
