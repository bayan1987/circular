package steep.circular.view;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import steep.circular.R;
import steep.circular.data.Event;

/**
 * Created by Tom Kretzschmar on 15.11.2017.
 *
 *
 */

public class DetailsLayout  extends CoordinatorLayout {

    public CardView cardViewContainer;
    public TextView textViewTitle;
    public TextView textViewDescription;

    public DetailsLayout(Context context) {
        super(context);
    }

    public DetailsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        cardViewContainer = (CardView) findViewById(R.id.cardView);
        textViewTitle = (TextView) findViewById(R.id.titleView);
        textViewDescription = (TextView) findViewById(R.id.dateView);
    }

    private void setData(Event event) {
        textViewTitle.setText(event.getTitle());
        textViewDescription.setText(event.getDate().toString());
    }

    public static Scene showScene(Activity activity, final ViewGroup container, final View sharedView, final String transitionName, final Event data) {
        DetailsLayout detailsLayout = (DetailsLayout) activity.getLayoutInflater().inflate(R.layout.details_layout, container, false);
        detailsLayout.setData(data);

        TransitionSet set = new ShowDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout);
        Scene scene = new Scene(container, (View) detailsLayout);
        TransitionManager.go(scene, set);
        return scene;
    }

//    public static Scene hideScene(Activity activity, final ViewGroup container, final View sharedView, final String transitionName) {
//        DetailsLayout detailsLayout = (DetailsLayout) container.findViewById(R.id.details_container);
//
//        TransitionSet set = new HideDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout);
//        Scene scene = new Scene(container, (View) detailsLayout);
//        TransitionManager.go(scene, set);
//        return scene;
//    }
}
