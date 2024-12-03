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
import org.jfree.date.SerialDate;

public class Month
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = -5090216912548722570L;
    private int month;
    private int year;
    private long firstMillisecond;
    private long lastMillisecond;

    public Month() {
        this(new Date());
    }

    public Month(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year;
        this.peg(Calendar.getInstance());
    }

    public Month(int month, Year year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year.getYear();
        this.peg(Calendar.getInstance());
    }

    public Month(Date time) {
        this(time, TimeZone.getDefault());
    }

    public Month(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Month(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.month = calendar.get(2) + 1;
        this.year = calendar.get(1);
        this.peg(calendar);
    }

    public Year getYear() {
        return new Year(this.year);
    }

    public int getYearValue() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
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
        Month result = this.month != 1 ? new Month(this.month - 1, this.year) : (this.year > 1900 ? new Month(12, this.year - 1) : null);
        return result;
    }

    public RegularTimePeriod next() {
        Month result = this.month != 12 ? new Month(this.month + 1, this.year) : (this.year < 9999 ? new Month(1, this.year + 1) : null);
        return result;
    }

    public long getSerialIndex() {
        return (long)this.year * 12L + (long)this.month;
    }

    public String toString() {
        return SerialDate.monthCodeToString(this.month) + " " + this.year;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Month)) {
            return false;
        }
        Month that = (Month)obj;
        if (this.month != that.month) {
            return false;
        }
        return this.year == that.year;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.month;
        result = 37 * result + this.year;
        return result;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Month) {
            Month m = (Month)o1;
            result = this.year - m.getYearValue();
            if (result == 0) {
                result = this.month - m.getMonth();
            }
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.year, this.month - 1, 1, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        int eom = SerialDate.lastDayOfMonth(this.month, this.year);
        calendar.set(this.year, this.month - 1, eom, 23, 59, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }

    public static Month parseMonth(String s) {
        int month;
        Year year;
        String s2;
        String s1;
        boolean yearIsFirst;
        Month result = null;
        if (s == null) {
            return result;
        }
        int i = Month.findSeparator(s = s.trim());
        if (i == -1) {
            yearIsFirst = true;
            s1 = s.substring(0, 5);
            s2 = s.substring(5);
        } else {
            s1 = s.substring(0, i).trim();
            s2 = s.substring(i + 1, s.length()).trim();
            Year y1 = Month.evaluateAsYear(s1);
            if (y1 == null) {
                yearIsFirst = false;
            } else {
                Year y2 = Month.evaluateAsYear(s2);
                if (y2 == null) {
                    yearIsFirst = true;
                } else {
                    boolean bl = yearIsFirst = s1.length() > s2.length();
                }
            }
        }
        if (yearIsFirst) {
            year = Month.evaluateAsYear(s1);
            month = SerialDate.stringToMonthCode(s2);
        } else {
            year = Month.evaluateAsYear(s2);
            month = SerialDate.stringToMonthCode(s1);
        }
        if (month == -1) {
            throw new TimePeriodFormatException("Can't evaluate the month.");
        }
        if (year == null) {
            throw new TimePeriodFormatException("Can't evaluate the year.");
        }
        result = new Month(month, year);
        return result;
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
}

