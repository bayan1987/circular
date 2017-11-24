package steep.circular.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import steep.circular.R;
import steep.circular.data.Divider;
import steep.circular.data.Event;
import steep.circular.data.EventAdapter;
import steep.circular.data.MyDate;
import steep.circular.data.RecyclerItem;
import steep.circular.dialog.CalendarDialog;
import steep.circular.dialog.DialogListener;
import steep.circular.service.CalendarService;
import steep.circular.view.CircleCalendarView;

public class MainActivity extends AppCompatActivity implements DialogListener {

    public static final int READ_CALENDAR_REQUEST = 1;
    public static final int TARGET_ALPHA = 72;


    CircleCalendarView view;
    CalendarService calSrv;
    private RecyclerView mRecyclerView;
    private List<RecyclerItem>  evList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    READ_CALENDAR_REQUEST);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        MyDate start = MyDate.getToday();
        MyDate end = MyDate.getToday();
        end.setYear(end.getYear() + 1);

        calSrv = new CalendarService(getApplicationContext());
        List<List<Event>> list = calSrv.getEventPerDayList(start, end);
        view = (CircleCalendarView) findViewById(R.id.calendar);
        view.setEvents(list);
        view.setActivity(this);

        evList = new ArrayList<>();
        for(List<Event> l : list){
            evList.addAll(l);
            if (!l.isEmpty()) evList.add(new Divider("date"));
        } // TODO: anstelle eines dividers, erstellen eines custom layouts für jeden tag, wo das datum als balken oben drüber steht. somit erigbt sich in der liste für den recyclerview nur ein eintrag pro tag. dadurch auch evtl besseres scrollen?

//        List<Event> evList = list.get(start.getDayOfYear());
        Log.d("recycler", "evlist:" + evList.size());
        EventAdapter adapter2 = new EventAdapter(this, evList);


        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.event_day_list);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter2);

        Resources r = getResources();
        int px =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, r.getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "SETTINGS", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, SettingsActivity.class);
//            String message = editText.getText().toString();
//            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);

            return true;
        }
        if(id == R.id.filter_setting) {
            Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show();
            Log.d("Filter", "Settings");

            DialogFragment dialog = new CalendarDialog();
            dialog.show(getFragmentManager(), "calendarSelection");


        }
        if(id == R.id.today){
            view.resetPointer();
            view.invalidate();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_CALENDAR_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.d("Permission", "Permission granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Permission", "Permission denied");

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onPositiveDialogReturn() {
        MyDate start = MyDate.getToday();
        MyDate end = MyDate.getToday();
        end.setYear(end.getYear() + 1);


        List<List<Event>> list = calSrv.getEventPerDayList(start, end);

        view.setEvents(list);
        view.invalidate();

        // TODO: bottom View list und current day list aktualisieren
    }

    @Override
    public void onNegativeDialogReturn() {

    }

    // TODO: scrollen ist falsch, da viel mehr events als tage vorhanden sind. es wird aber jedes event in der liste gezählt
    public void scroll(int dayofyear){

        int pos = dayofyear - MyDate.getToday().getDayOfYear();
        if(pos < 0) pos += 365;
        Log.d("scroll", "day: "  + dayofyear + " pos: " + pos);
        mRecyclerView.scrollToPosition(pos);
    }
}
