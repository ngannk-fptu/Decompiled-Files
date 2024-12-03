/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;

public class Hour
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = -835471579831937652L;
    public static final int FIRST_HOUR_IN_DAY = 0;
    public static final int LAST_HOUR_IN_DAY = 23;
    private Day day;
    private byte hour;
    private long firstMillisecond;
    private long lastMillisecond;

    public Hour() {
        this(new Date());
    }

    public Hour(int hour, Day day) {
        if (day == null) {
            throw new IllegalArgumentException("Null 'day' argument.");
        }
        this.hour = (byte)hour;
        this.day = day;
        this.peg(Calendar.getInstance());
    }

    public Hour(int hour, int day, int month, int year) {
        this(hour, new Day(day, month, year));
    }

    public Hour(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Hour(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Hour(Date time, TimeZone zone, Locale locale) {
        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Null 'locale' argument.");
        }
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.hour = (byte)calendar.get(11);
        this.day = new Day(time, zone, locale);
        this.peg(calendar);
    }

    public int getHour() {
        return this.hour;
    }

    public Day getDay() {
        return this.day;
    }

    public int getYear() {
        return this.day.getYear();
    }

    public int getMonth() {
        return this.day.getMonth();
    }

    public int getDayOfMonth() {
        return this.day.getDayOfMonth();
    }

    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    public long getLastMillisecond() {
        return this.lastMillisecond;
    }

    public void peg(Calendar calendar) {
        this.firstMillisecond = this.getFirstMillisecond(calendar);
        this.lastMillisecond = this.getLastMillisecond(calendar);
    }

    public RegularTimePeriod previous() {
        Day prevDay;
        Hour result = this.hour != 0 ? new Hour(this.hour - 1, this.day) : ((prevDay = (Day)this.day.previous()) != null ? new Hour(23, prevDay) : null);
        return result;
    }

    public RegularTimePeriod next() {
        Day nextDay;
        Hour result = this.hour != 23 ? new Hour(this.hour + 1, this.day) : ((nextDay = (Day)this.day.next()) != null ? new Hour(0, nextDay) : null);
        return result;
    }

    public long getSerialIndex() {
        return this.day.getSerialIndex() * 24L + (long)this.hour;
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int dom = this.day.getDayOfMonth();
        calendar.set(year, month, dom, this.hour, 0, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int dom = this.day.getDayOfMonth();
        calendar.set(year, month, dom, this.hour, 59, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Hour)) {
            return false;
        }
        Hour that = (Hour)obj;
        if (this.hour != that.hour) {
            return false;
        }
        return this.day.equals(that.day);
    }

    public String toString() {
        return "[" + this.hour + "," + this.getDayOfMonth() + "/" + this.getMonth() + "/" + this.getYear() + "]";
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Hour) {
            Hour h = (Hour)o1;
            result = this.getDay().compareTo(h.getDay());
            if (result == 0) {
                result = this.hour - h.getHour();
            }
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public static Hour parseHour(String s) {
        Hour result = null;
        String daystr = (s = s.trim()).substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hourstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length());
            int hour = Integer.parseInt(hourstr = hourstr.trim());
            if (hour >= 0 && hour <= 23) {
                result = new Hour(hour, day);
            }
        }
        return result;
    }
}

