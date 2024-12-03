/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.date.SerialDate;

public class Day
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = -7082667380758962755L;
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    protected static final DateFormat DATE_FORMAT_SHORT = DateFormat.getDateInstance(3);
    protected static final DateFormat DATE_FORMAT_MEDIUM = DateFormat.getDateInstance(2);
    protected static final DateFormat DATE_FORMAT_LONG = DateFormat.getDateInstance(1);
    private SerialDate serialDate;
    private long firstMillisecond;
    private long lastMillisecond;

    public Day() {
        this(new Date());
    }

    public Day(int day, int month, int year) {
        this.serialDate = SerialDate.createInstance(day, month, year);
        this.peg(Calendar.getInstance());
    }

    public Day(SerialDate serialDate) {
        if (serialDate == null) {
            throw new IllegalArgumentException("Null 'serialDate' argument.");
        }
        this.serialDate = serialDate;
        this.peg(Calendar.getInstance());
    }

    public Day(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Day(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Day(Date time, TimeZone zone, Locale locale) {
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
        int d = calendar.get(5);
        int m = calendar.get(2) + 1;
        int y = calendar.get(1);
        this.serialDate = SerialDate.createInstance(d, m, y);
        this.peg(calendar);
    }

    public SerialDate getSerialDate() {
        return this.serialDate;
    }

    public int getYear() {
        return this.serialDate.getYYYY();
    }

    public int getMonth() {
        return this.serialDate.getMonth();
    }

    public int getDayOfMonth() {
        return this.serialDate.getDayOfMonth();
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
        int serial = this.serialDate.toSerial();
        if (serial > 2) {
            SerialDate yesterday = SerialDate.createInstance(serial - 1);
            return new Day(yesterday);
        }
        RegularTimePeriod result = null;
        return result;
    }

    public RegularTimePeriod next() {
        int serial = this.serialDate.toSerial();
        if (serial < 2958465) {
            SerialDate tomorrow = SerialDate.createInstance(serial + 1);
            return new Day(tomorrow);
        }
        RegularTimePeriod result = null;
        return result;
    }

    public long getSerialIndex() {
        return this.serialDate.toSerial();
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 23, 59, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Day)) {
            return false;
        }
        Day that = (Day)obj;
        return this.serialDate.equals(that.getSerialDate());
    }

    public int hashCode() {
        return this.serialDate.hashCode();
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Day) {
            Day d = (Day)o1;
            result = -d.getSerialDate().compare(this.serialDate);
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public String toString() {
        return this.serialDate.toString();
    }

    public static Day parseDay(String s) {
        try {
            return new Day(DATE_FORMAT.parse(s));
        }
        catch (ParseException e1) {
            try {
                return new Day(DATE_FORMAT_SHORT.parse(s));
            }
            catch (ParseException parseException) {
                return null;
            }
        }
    }
}

