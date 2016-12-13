package steep.circular.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import steep.circular.data.Event;
import steep.circular.data.MyDate;
import steep.circular.service.CalendarService;
import steep.circular.view.CircleCalendarView;

import static steep.circular.TabbedMainActivity.READ_CALENDAR_REQUEST;

/**
 * Created by Tom on 10.11.2016.
 *
 */

public class CalendarFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CircleCalendarView view;
    private List<List<Event>> list;

    public CalendarFragment() {
//        Thread calendarQueryThread;
//
//
//
//        Runnable calQueryRunnable = new Runnable() {
//            @Override
//            public void run() {

//            CalendarService calSrv = new CalendarService(getContext().getApplicationContext());
//            list = calSrv.getEventPerDayList(new MyDate(30,11,2016), new MyDate(20,11,2017));
//            }
//        };
//        calendarQueryThread = new Thread(calQueryRunnable);
//        calendarQueryThread.start();

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CalendarFragment newInstance(int sectionNumber) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_tabbed_main, container, false);
//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//        return rootView;


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALENDAR},
                    READ_CALENDAR_REQUEST);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        MyDate start = MyDate.getToday();
        MyDate end = MyDate.getToday();
        end.setYear(end.getYear()+1);

        CalendarService calSrv = new CalendarService(getContext().getApplicationContext());
        list = calSrv.getEventPerDayList(start, end);
        view = new CircleCalendarView(this.getActivity().getApplicationContext());
        view.setEvents(list);
        return view;
    }
}
