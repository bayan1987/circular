package steep.circular.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom Kretzschmar on 26.10.2016.
 *
 */

public enum Weekday {
    Monday(0), Tuesday(1), Wednesday(2), Thursday(3), Friday(4), Saturday(5), Sunday(6);

    private final int value;
    private static Map<Integer, Weekday> map = new HashMap<>();

    Weekday(int value) {
        this.value = value;
    }

    static {
        for (Weekday weekDay : Weekday.values()) {
            map.put(weekDay.getValue(), weekDay);
        }
    }

    public static Weekday valueOf(int weekDay) {
        return map.get(weekDay);
    }

    public int getValue() {
        return value;
    }
}
