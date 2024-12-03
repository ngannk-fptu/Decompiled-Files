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
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;

public class Minute
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = 2144572840034842871L;
    public static final int FIRST_MINUTE_IN_HOUR = 0;
    public static final int LAST_MINUTE_IN_HOUR = 59;
    private Day day;
    private byte hour;
    private byte minute;
    private long firstMillisecond;
    private long lastMillisecond;

    public Minute() {
        this(new Date());
    }

    public Minute(int minute, Hour hour) {
        if (hour == null) {
            throw new IllegalArgumentException("Null 'hour' argument.");
        }
        this.minute = (byte)minute;
        this.hour = (byte)hour.getHour();
        this.day = hour.getDay();
        this.peg(Calendar.getInstance());
    }

    public Minute(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Minute(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Minute(Date time, TimeZone zone, Locale locale) {
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
        int min = calendar.get(12);
        this.minute = (byte)min;
        this.hour = (byte)calendar.get(11);
        this.day = new Day(time, zone, locale);
        this.peg(calendar);
    }

    public Minute(int minute, int hour, int day, int month, int year) {
        this(minute, new Hour(hour, new Day(day, month, year)));
    }

    public Day getDay() {
        return this.day;
    }

    public Hour getHour() {
        return new Hour(this.hour, this.day);
    }

    public int getHourValue() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
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
        Hour h;
        Minute result = this.minute != 0 ? new Minute(this.minute - 1, this.getHour()) : ((h = (Hour)this.getHour().previous()) != null ? new Minute(59, h) : null);
        return result;
    }

    public RegularTimePeriod next() {
        Hour nextHour;
        Minute result = this.minute != 59 ? new Minute(this.minute + 1, this.getHour()) : ((nextHour = (Hour)this.getHour().next()) != null ? new Minute(0, nextHour) : null);
        return result;
    }

    public long getSerialIndex() {
        long hourIndex = this.day.getSerialIndex() * 24L + (long)this.hour;
        return hourIndex * 60L + (long)this.minute;
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int day = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, day, this.hour, this.minute, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int day = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, day, this.hour, this.minute, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Minute)) {
            return false;
        }
        Minute that = (Minute)obj;
        if (this.minute != that.minute) {
            return false;
        }
        return this.hour == that.hour;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.minute;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Minute) {
            Minute m = (Minute)o1;
            result = this.getHour().compareTo(m.getHour());
            if (result == 0) {
                result = this.minute - m.getMinute();
            }
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public static Minute parseMinute(String s) {
        Minute result = null;
        String daystr = (s = s.trim()).substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String minstr;
            int minute;
            String hmstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length());
            String hourstr = (hmstr = hmstr.trim()).substring(0, Math.min(2, hmstr.length()));
            int hour = Integer.parseInt(hourstr);
            if (hour >= 0 && hour <= 23 && (minute = Integer.parseInt(minstr = hmstr.substring(Math.min(hourstr.length() + 1, hmstr.length()), hmstr.length()))) >= 0 && minute <= 59) {
                result = new Minute(minute, new Hour(hour, day));
            }
        }
        return result;
    }
}

