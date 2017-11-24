package steep.circular.data;

/**
 * Created by Tom Kretzschmar on 24.11.2017.
 *
 */

public abstract class RecyclerItem {

    public static final int TYPE_DIVIDER = 0;
    public static final int TYPE_EVENT = 1;

    abstract public int getType();

}
