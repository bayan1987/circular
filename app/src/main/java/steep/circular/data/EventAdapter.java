package steep.circular.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import steep.circular.R;

/**
 * Created by Tom Kretzschmar on 18.01.2017.
 *
 */

public class EventAdapter extends
        RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public Button eventButtonView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            eventButtonView = (Button) itemView.findViewById(R.id.button_event);
        }
    }

    // Store a member variable for the contacts
    private List<Event> eventList;
    // Store the context for easy access
    private Context context;

    // Pass in the contact array into the constructor
    public EventAdapter(Context context, List<Event> contacts) {
        eventList = contacts;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.item_event, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Event event = eventList.get(position);

        // Set item views based on your views and data model
        Button button = viewHolder.eventButtonView;
        button.setText(event.getTitle());
        Log.d("recycler", "title:" + event.getTitle());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d("recycler", "itemcount:" + eventList.size());
        return eventList.size();
    }

}