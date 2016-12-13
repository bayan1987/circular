package steep.circular.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Set;

/**
 * Created by Tom Kretzschmar on 13.12.2016.
 *
 */

public class PreferenceService{

    private static final String PREFERENCE_TAG = "de.steep.circular.preferences";
    private static final String PREF_KEY_CALENDARS = "de.steep.circular.calendars";

    private SharedPreferences preferences;

    public PreferenceService(Context context){
        preferences = context.getSharedPreferences(PREFERENCE_TAG, 0);
    }

    public void putCalendars(@NonNull Set<String> calendars){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(PREF_KEY_CALENDARS, calendars);
        editor.apply();
    }

    public Set<String> getCalendars() {
        final Set<String> calendars = preferences.getStringSet(PREF_KEY_CALENDARS, null);
        return calendars;
    }

    public void resetCalendars(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_KEY_CALENDARS);
        editor.apply();
    }
}
