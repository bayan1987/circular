package steep.circular.data;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tom Kretzschmar on 30.11.2016.
 *
 */

public class MyDate {

    private int day;
    private int month;
    private int year;

    public MyDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public MyDate(long timeInMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH)+1;
        year = cal.get(Calendar.YEAR);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyDate myDate = (MyDate) o;

        if (day != myDate.day) return false;
        if (month != myDate.month) return false;
        return year == myDate.year;

    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + month;
        result = 31 * result + year;
        return result;
    }

    public MyDate(Date date) {
        this(date.getTime());

    }

    public long getTimeInMillis(){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);
        return cal.getTimeInMillis();
    }

    public Date getJavaUtilDate(){
        return new Date(getTimeInMillis());
    }

    public int getDayOfYear(){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
