package steep.circular.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom  Kretzschmar on 26.10.2016.
 *
 */

public enum Month {
    January(1), February(2), March(3), April(4), May(5), June(6), July(7), August(8), September(9), October(10), November(11), December(12);

    private final int value;
    private static Map<Integer, Month> map = new HashMap<>();
    private static Integer[] daysOfMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    Month(int value) {
        this.value = value;
    }

    static {
        for (Month month : Month.values()) {
            map.put(month.getValue(), month);
        }
    }

    public int getDayCount() {
        return daysOfMonths[this.getValue() - 1];
    }

    public int getValue() {
        return value;
    }
}
