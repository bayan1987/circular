package steep.circular.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import steep.circular.data.CalendarService;
import steep.circular.data.LightweightEvent;
import steep.circular.data.MyDate;
import steep.circular.view.CircleCalendarView;

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
    private List<List<LightweightEvent>> list;

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
        CalendarService calSrv = new CalendarService(getContext().getApplicationContext());
        list = calSrv.getEventPerDayList(new MyDate(30,11,2016), new MyDate(20,11,2017));
        view = new CircleCalendarView(this.getActivity().getApplicationContext());
        view.setEvents(list);
        return view;
    }
}
