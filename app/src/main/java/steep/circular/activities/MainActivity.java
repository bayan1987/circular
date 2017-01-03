package steep.circular.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import steep.circular.R;
import steep.circular.data.Event;
import steep.circular.data.MyDate;
import steep.circular.dialog.CalendarDialog;
import steep.circular.dialog.DialogListener;
import steep.circular.service.CalendarService;
import steep.circular.view.CircleCalendarView;

public class MainActivity extends AppCompatActivity implements DialogListener {

    public static final int READ_CALENDAR_REQUEST = 1;

    private BottomSheetBehavior bottomSheetBehavior;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<>();


    CircleCalendarView view;
    CalendarService calSrv;

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



        View bottomSheet = findViewById(R.id.bottom_sheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ListView listView = (ListView) findViewById(R.id.event_list);


        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView.setAdapter(adapter);
        listView.getParent().requestDisallowInterceptTouchEvent(true);



        for (int i = 0; i < 365; i++) { // TODO daysinyear
            if (list != null && list.get(i) != null) {
                for (int j = 0; j < list.get(i).size(); j++) {
                    Event event = list.get(i).get(j);
                    if(event.getDate() != null) {
                        MyDate date = new MyDate(event.getDate());
                        adapter.add(date + " | " + event.getTitle());
                    } else {
                        adapter.add("null" + " | " + event.getTitle());

                    }
                }
            }
        }


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
        if(id == R.id.bottomSheetSetting){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if(id == R.id.filter_setting) {
            Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show();
            Log.d("Filter", "Settings");

            DialogFragment dialog = new CalendarDialog();
            dialog.show(getFragmentManager(), "calendarSelection");


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
    }

    @Override
    public void onNegativeDialogReturn() {

    }
}
