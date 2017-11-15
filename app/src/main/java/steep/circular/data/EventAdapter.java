package steep.circular.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import steep.circular.R;
import steep.circular.activities.OnEventClickListener;
import steep.circular.activities.TransitionUtils;

import static android.R.attr.button;
import static android.media.CamcorderProfile.get;

/**
 * Created by Tom Kretzschmar on 18.01.2017.
 *
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private OnEventClickListener listener;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    static class EventViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        View eventButtonView;
        TextView eventText;
        TextView eventTime;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        EventViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any EventViewHolder instance.
            super(itemView);
            eventButtonView = itemView.findViewById(R.id.event_item);
            eventText = (TextView) itemView.findViewById(R.id.event_text);
            eventTime = (TextView) itemView.findViewById(R.id.event_time);
        }
    }

    // Store a member variable for the contacts
    private List<Event> eventList;
    // Store the context for easy access
    private Context context;

    // Pass in the contact array into the constructor
    public EventAdapter(Context context, List<Event> contacts, OnEventClickListener listener) {
        eventList = contacts;
        this.context = context;
        this.listener = listener;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.item_event, parent, false);

        // Return a new holder instance
        EventViewHolder viewHolder = new EventViewHolder(eventView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Event event = eventList.get(position);

        // Set item views based on your views and data model
        final View item = viewHolder.eventButtonView;
        viewHolder.eventText.setText(event.getTitle());
        if(event.getDate()!= null){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            viewHolder.eventTime.setText(format.format(event.getDate()));
        }

        item.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          listener.onPlaceClicked(item, TransitionUtils.getRecyclerViewTransitionName(position), position);
                                      }
                                  });
        Log.d("recycler", "title:" + event.getTitle());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d("recycler", "itemcount:" + eventList.size());
        return eventList.size();
    }

}