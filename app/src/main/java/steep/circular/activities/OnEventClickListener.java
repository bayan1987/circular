package steep.circular.activities;

import android.view.View;
import android.widget.Button;

/**
 * Created by Tom Kretzschmar on 11.11.2017.
 *
 */

public interface OnEventClickListener {
    void onPlaceClicked(View sharedView, String transitionName, final int position);
}
