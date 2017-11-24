package steep.circular.data;

/**
 * Created by Tom Kretzschmar on 24.11.2017.
 *
 */

public class Divider extends RecyclerItem{

    public String date;

    public Divider(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DIVIDER;
    }
}
