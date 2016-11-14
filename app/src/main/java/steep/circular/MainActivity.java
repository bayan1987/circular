package steep.circular;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import steep.circular.data.CalendarService;
import steep.circular.data.LightweightEvent;
import steep.circular.view.CircleCalendarView;
import steep.circular.view.CircleClockView;

public class MainActivity extends AppCompatActivity {

    private CircleClockView cView;
    private CircleCalendarView view;

    private  Handler handler;

    private boolean clock = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("Toggle")){
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
            if(!clock) {
                clock = true;
                layout.removeView(view);
                layout.addView(cView);
                layout.setBackgroundColor(Color.BLACK);
            } else {
                clock = false;
                layout.removeView(cView);
                layout.addView(view);
                layout.setBackgroundColor(Color.WHITE);

            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = new CircleCalendarView(this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        layout.addView(view);

        cView = new CircleClockView(this, null);

//        layout = (RelativeLayout) findViewById(R.id.activity_main);
//        layout.addView(cView);


         handler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(Message msg) {
                update();
            }
        };

        Thread t;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message m = Message.obtain(handler);
                    handler.sendMessage(m);
                }
//                for(int j = 0; j < 60; j++) {
//                    for (int i = 0; i < 6000; i++) {
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Message m = new Message();
//                        m.arg1 = i;
//                        handler.sendMessage(m);
//                    }
//                }
            }
        };


        t = new Thread(runnable);
        t.start();

        Thread calendarQueryThread;
        Runnable calQueryRunnable = new Runnable() {
            @Override
            public void run() {

                Date start = new Date(1478294632055l);
                Date end = new Date(1485810000000l);

                CalendarService calSrv = new CalendarService(getApplicationContext());
                List<steep.circular.data.Calendar> calendars = calSrv.getAllEvents(start, end);
                List<LightweightEvent> lightEvents = calSrv.getLightweightEvents(start, end);

                for(LightweightEvent event : lightEvents){
                    Log.d("Cal", "LightEvent: [" + event.getDate() + "] - " + event.getTitle() + " - " + event.getId() + " - " + event.getCal_id() + " - " + event.getCal_title());
                }
            }
        };


        calendarQueryThread = new Thread(calQueryRunnable);
        calendarQueryThread.start();

    }

    private void update(){
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE);
        int second = java.util.Calendar.getInstance().get(java.util.Calendar.SECOND);
        int millisecond = java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND);
        if(cView != null) {
            cView.updateClock(hour, minute, second, millisecond);
        }
    }
}
