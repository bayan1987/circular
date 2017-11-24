package steep.circular.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import steep.circular.R;

/**
 * Created by Tom Kretzschmar on 18.01.2017.
 *
 */

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


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

    static class DividerViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        View dividerView;
        TextView dividerDate;

        DividerViewHolder(View divider) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any EventViewHolder instance.
            super(divider);
            dividerView = itemView.findViewById(R.id.divider_item);
            dividerDate = (TextView) itemView.findViewById(R.id.divider_date);
        }
    }

    // Store a member variable for the contacts
    private List<RecyclerItem> eventList;
    // Store the context for easy access
    private Context context;

    // Pass in the contact array into the constructor
    public EventAdapter(Context context, List<RecyclerItem> contacts) {
        eventList = contacts;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == RecyclerItem.TYPE_EVENT){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View eventView = inflater.inflate(R.layout.item_event, parent, false);

            // Return a new holder instance
            EventViewHolder viewHolder = new EventViewHolder(eventView);
            return viewHolder;
        } else {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View dividerView = inflater.inflate(R.layout.item_divider, parent, false);

            // Return a new holder instance
            DividerViewHolder viewHolder = new DividerViewHolder(dividerView);
            return viewHolder;
        }
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int type = getItemViewType(position);

        if(type == RecyclerItem.TYPE_EVENT){
            // Get the data model based on position
            Event event = (Event) eventList.get(position);

            EventViewHolder viewHolder = (EventViewHolder) holder;


            // Set item views based on your views and data model
            final View item = viewHolder.eventButtonView;
            viewHolder.eventText.setText(event.getTitle());
            if(event.getDate()!= null){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                viewHolder.eventTime.setText(format.format(event.getDate()));
            }

            Log.d("recycler", "title:" + event.getTitle());
        } else {
            // Get the data model based on position
            Divider divider = (Divider) eventList.get(position);

            DividerViewHolder viewHolder = (DividerViewHolder) holder;


            viewHolder.dividerDate.setText(divider.date);
            // Set item views based on your views and data model
            final View item = viewHolder.dividerView;

        }


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d("recycler", "itemcount:" + eventList.size());
        return eventList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return eventList.get(position).getType();
    }
}