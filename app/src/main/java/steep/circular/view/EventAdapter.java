package steep.circular.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import steep.circular.R;
import steep.circular.data.Event;

/**
 * Created by Tom on 18.01.2017.
 */

public class EventAdapter extends
        RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            messageButton = (Button) itemView.findViewById(R.id.button_event);
        }
    }

    // Store a member variable for the contacts
    private List<Event> mContacts;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public EventAdapter(Context context, List<Event> contacts) {
        mContacts = contacts;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_event, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Event contact = mContacts.get(position);

        // Set item views based on your views and data model
        Button button = viewHolder.messageButton;
        button.setText(contact.getTitle());
        Log.d("recycler", "title:" + contact.getTitle());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d("recycler", "itemcount:" + mContacts.size());
        return mContacts.size();
    }

}