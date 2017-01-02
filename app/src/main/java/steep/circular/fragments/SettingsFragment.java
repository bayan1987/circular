package steep.circular.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Set;

import steep.circular.R;
import steep.circular.data.Calendar;
import steep.circular.service.CalendarService;
import steep.circular.service.PreferenceService;

public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        addPreferencesFromResource(R.xml.pref_general);
//        addPreferencesFromResource(R.xml.pref_notification);
//        addPreferencesFromResource(R.xml.pref_data_sync);


        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this.getActivity().getApplicationContext());

        PreferenceCategory category = new PreferenceCategory(this.getActivity().getApplicationContext());
        category.setTitle("Show Calendars");

        screen.addPreference(category);


        PreferenceService preferenceService = new PreferenceService(this.getActivity().getApplicationContext());
        Set<String> prefCalendars = preferenceService.getCalendars();


        CalendarService calSrv = new CalendarService(this.getActivity().getApplicationContext());
        List<Calendar> calendars = calSrv.getAvailableCalendars();
        for(Calendar cal: calendars){
            CheckBoxPreference checkBoxPref = new CheckBoxPreference(this.getActivity().getApplicationContext());
            checkBoxPref.setTitle(cal.getTitle());


            if(prefCalendars.contains(cal.getTitle())) {
                checkBoxPref.setChecked(true);
            }

            category.addPreference(checkBoxPref);
        }


        setPreferenceScreen(screen);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
