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
import org.jfree.data.time.Year;

public class Week
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = 1856387786939865061L;
    public static final int FIRST_WEEK_IN_YEAR = 1;
    public static final int LAST_WEEK_IN_YEAR = 53;
    private short year;
    private byte week;
    private long firstMillisecond;
    private long lastMillisecond;

    public Week() {
        this(new Date());
    }

    public Week(int week, int year) {
        if (week < 1 && week > 53) {
            throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53.");
        }
        this.week = (byte)week;
        this.year = (short)year;
        this.peg(Calendar.getInstance());
    }

    public Week(int week, Year year) {
        if (week < 1 && week > 53) {
            throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53.");
        }
        this.week = (byte)week;
        this.year = (short)year.getYear();
        this.peg(Calendar.getInstance());
    }

    public Week(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Week(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Week(Date time, TimeZone zone, Locale locale) {
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
        int tempWeek = calendar.get(3);
        if (tempWeek == 1 && calendar.get(2) == 11) {
            this.week = 1;
            this.year = (short)(calendar.get(1) + 1);
        } else {
            this.week = (byte)Math.min(tempWeek, 53);
            int yyyy = calendar.get(1);
            if (calendar.get(2) == 0 && this.week >= 52) {
                --yyyy;
            }
            this.year = (short)yyyy;
        }
        this.peg(calendar);
    }

    public Year getYear() {
        return new Year(this.year);
    }

    public int getYearValue() {
        return this.year;
    }

    public int getWeek() {
        return this.week;
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
        Week result;
        if (this.week != 1) {
            result = new Week(this.week - 1, this.year);
        } else if (this.year > 1900) {
            int yy = this.year - 1;
            Calendar prevYearCalendar = Calendar.getInstance();
            prevYearCalendar.set(yy, 11, 31);
            result = new Week(prevYearCalendar.getActualMaximum(3), yy);
        } else {
            result = null;
        }
        return result;
    }

    public RegularTimePeriod next() {
        Week result;
        if (this.week < 52) {
            result = new Week(this.week + 1, this.year);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(this.year, 11, 31);
            int actualMaxWeek = calendar.getActualMaximum(3);
            result = this.week < actualMaxWeek ? new Week(this.week + 1, this.year) : (this.year < 9999 ? new Week(1, this.year + 1) : null);
        }
        return result;
    }

    public long getSerialIndex() {
        return (long)this.year * 53L + (long)this.week;
    }

    public long getFirstMillisecond(Calendar calendar) {
        Calendar c = (Calendar)calendar.clone();
        c.clear();
        c.set(1, this.year);
        c.set(3, this.week);
        c.set(7, c.getFirstDayOfWeek());
        c.set(10, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        Calendar c = (Calendar)calendar.clone();
        c.clear();
        c.set(1, this.year);
        c.set(3, this.week + 1);
        c.set(7, c.getFirstDayOfWeek());
        c.set(10, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime().getTime() - 1L;
    }

    public String toString() {
        return "Week " + this.week + ", " + this.year;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Week)) {
            return false;
        }
        Week that = (Week)obj;
        if (this.week != that.week) {
            return false;
        }
        return this.year == that.year;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.week;
        result = 37 * result + this.year;
        return result;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Week) {
            Week w = (Week)o1;
            result = this.year - w.getYear().getYear();
            if (result == 0) {
                result = this.week - w.getWeek();
            }
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Week parseWeek(String s) {
        Week result = null;
        if (s == null) return result;
        int i = Week.findSeparator(s = s.trim());
        if (i == -1) throw new TimePeriodFormatException("Could not find separator.");
        String s1 = s.substring(0, i).trim();
        String s2 = s.substring(i + 1, s.length()).trim();
        Year y = Week.evaluateAsYear(s1);
        if (y != null) {
            int w = Week.stringToWeek(s2);
            if (w != -1) return new Week(w, y);
            throw new TimePeriodFormatException("Can't evaluate the week.");
        }
        y = Week.evaluateAsYear(s2);
        if (y == null) throw new TimePeriodFormatException("Can't evaluate the year.");
        int w = Week.stringToWeek(s1);
        if (w != -1) return new Week(w, y);
        throw new TimePeriodFormatException("Can't evaluate the week.");
    }

    private static int findSeparator(String s) {
        int result = s.indexOf(45);
        if (result == -1) {
            result = s.indexOf(44);
        }
        if (result == -1) {
            result = s.indexOf(32);
        }
        if (result == -1) {
            result = s.indexOf(46);
        }
        return result;
    }

    private static Year evaluateAsYear(String s) {
        Year result = null;
        try {
            result = Year.parseYear(s);
        }
        catch (TimePeriodFormatException timePeriodFormatException) {
            // empty catch block
        }
        return result;
    }

    private static int stringToWeek(String s) {
        int result = -1;
        s = s.replace('W', ' ');
        s = s.trim();
        try {
            result = Integer.parseInt(s);
            if (result < 1 || result > 53) {
                result = -1;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return result;
    }
}

