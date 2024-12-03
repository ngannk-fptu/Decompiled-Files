/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodFormatException;

public class Year
extends RegularTimePeriod
implements Serializable {
    public static final int MINIMUM_YEAR = -9999;
    public static final int MAXIMUM_YEAR = 9999;
    private static final long serialVersionUID = -7659990929736074836L;
    private short year;
    private long firstMillisecond;
    private long lastMillisecond;

    public Year() {
        this(new Date());
    }

    public Year(int year) {
        if (year < -9999 || year > 9999) {
            throw new IllegalArgumentException("Year constructor: year (" + year + ") outside valid range.");
        }
        this.year = (short)year;
        this.peg(Calendar.getInstance());
    }

    public Year(Date time) {
        this(time, TimeZone.getDefault());
    }

    public Year(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Year(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.year = (short)calendar.get(1);
        this.peg(calendar);
    }

    public int getYear() {
        return this.year;
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
        if (this.year > -9999) {
            return new Year(this.year - 1);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.year < 9999) {
            return new Year(this.year + 1);
        }
        return null;
    }

    public long getSerialIndex() {
        return this.year;
    }

    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.year, 0, 1, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        calendar.set(this.year, 11, 31, 23, 59, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Year)) {
            return false;
        }
        Year that = (Year)obj;
        return this.year == that.year;
    }

    public int hashCode() {
        int result = 17;
        short c = this.year;
        result = 37 * result + c;
        return result;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Year) {
            Year y = (Year)o1;
            result = this.year - y.getYear();
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public String toString() {
        return Integer.toString(this.year);
    }

    public static Year parseYear(String s) {
        int y;
        try {
            y = Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e) {
            throw new TimePeriodFormatException("Cannot parse string.");
        }
        try {
            return new Year(y);
        }
        catch (IllegalArgumentException e) {
            throw new TimePeriodFormatException("Year outside valid range.");
        }
    }
}

