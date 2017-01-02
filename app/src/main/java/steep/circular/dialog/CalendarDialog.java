package steep.circular.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import steep.circular.data.Calendar;
import steep.circular.service.CalendarService;
import steep.circular.service.PreferenceService;

/**
 * Created by Tom Kretzschmar on 02.01.2017.
 *
 */

public class CalendarDialog extends DialogFragment {

    ArrayList<Integer> mSelectedItems;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        PreferenceService preferenceService = new PreferenceService(this.getActivity().getApplicationContext());
        Set<String> prefCalendars = preferenceService.getCalendars();


        CalendarService calSrv = new CalendarService(this.getActivity().getApplicationContext());
        List<Calendar> calendars = calSrv.getAvailableCalendars();
        String[] calStrings = new String[calendars.size()];
        for(int i = 0; i<calendars.size(); i++){
            calStrings[i] = calendars.get(i).getTitle();
        }

        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Select Calendars")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(calStrings, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
