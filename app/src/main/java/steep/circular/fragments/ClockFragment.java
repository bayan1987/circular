package steep.circular.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import steep.circular.view.CircleClockView;

/**
 * Created by Tom Kretzschmar on 10.11.2016.
 *
 */

public class ClockFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CircleClockView cView;
    private Handler handler;

    public ClockFragment() {

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
    }

    private void update(){
        int minute = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE);
        int second = java.util.Calendar.getInstance().get(java.util.Calendar.SECOND);
        int millisecond = java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND);
        if(cView != null) {
            cView.updateClock(minute, second, millisecond);
        }
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ClockFragment newInstance(int sectionNumber) {
        ClockFragment fragment = new ClockFragment();
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
        cView = new CircleClockView(this.getActivity().getApplicationContext());
        return cView;
    }
}
