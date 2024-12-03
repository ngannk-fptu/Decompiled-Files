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

public class Quarter
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = 3810061714380888671L;
    public static final int FIRST_QUARTER = 1;
    public static final int LAST_QUARTER = 4;
    public static final int[] FIRST_MONTH_IN_QUARTER = new int[]{0, 1, 4, 7, 10};
    public static final int[] LAST_MONTH_IN_QUARTER = new int[]{0, 3, 6, 9, 12};
    private short year;
    private byte quarter;
    private long firstMillisecond;
    private long lastMillisecond;

    public Quarter() {
        this(new Date());
    }

    public Quarter(int quarter, int year) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter outside valid range.");
        }
        this.year = (short)year;
        this.quarter = (byte)quarter;
        this.peg(Calendar.getInstance());
    }

    public Quarter(int quarter, Year year) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter outside valid range.");
        }
        this.year = (short)year.getYear();
        this.quarter = (byte)quarter;
        this.peg(Calendar.getInstance());
    }

    public Quarter(Date time) {
        this(time, TimeZone.getDefault());
    }

    public Quarter(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Quarter(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        int month = calendar.get(2) + 1;
        this.quarter = (byte)SerialDate.monthCodeToQuarter(month);
        this.year = (short)calendar.get(1);
        this.peg(calendar);
    }

    public int getQuarter() {
        return this.quarter;
    }

    public Year getYear() {
        return new Year(this.year);
    }

    public int getYearValue() {
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
        Quarter result = this.quarter > 1 ? new Quarter(this.quarter - 1, this.year) : (this.year > 1900 ? new Quarter(4, this.year - 1) : null);
        return result;
    }

    public RegularTimePeriod next() {
        Quarter result = this.quarter < 4 ? new Quarter(this.quarter + 1, this.year) : (this.year < 9999 ? new Quarter(1, this.year + 1) : null);
        return result;
    }

    public long getSerialIndex() {
        return (long)this.year * 4L + (long)this.quarter;
    }

    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj instanceof Quarter) {
                Quarter target = (Quarter)obj;
                return this.quarter == target.getQuarter() && this.year == target.getYearValue();
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.quarter;
        result = 37 * result + this.year;
        return result;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof Quarter) {
            Quarter q = (Quarter)o1;
            result = this.year - q.getYearValue();
            if (result == 0) {
                result = this.quarter - q.getQuarter();
            }
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public String toString() {
        return "Q" + this.quarter + "/" + this.year;
    }

    public long getFirstMillisecond(Calendar calendar) {
        int month = FIRST_MONTH_IN_QUARTER[this.quarter];
        calendar.set(this.year, month - 1, 1, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    public long getLastMillisecond(Calendar calendar) {
        int month = LAST_MONTH_IN_QUARTER[this.quarter];
        int eom = SerialDate.lastDayOfMonth(month, this.year);
        calendar.set(this.year, month - 1, eom, 23, 59, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }

    public static Quarter parseQuarter(String s) {
        int i = s.indexOf("Q");
        if (i == -1) {
            throw new TimePeriodFormatException("Missing Q.");
        }
        if (i == s.length() - 1) {
            throw new TimePeriodFormatException("Q found at end of string.");
        }
        String qstr = s.substring(i + 1, i + 2);
        int quarter = Integer.parseInt(qstr);
        String remaining = s.substring(0, i) + s.substring(i + 2, s.length());
        remaining = remaining.replace('/', ' ');
        remaining = remaining.replace(',', ' ');
        remaining = remaining.replace('-', ' ');
        Year year = Year.parseYear(remaining.trim());
        Quarter result = new Quarter(quarter, year);
        return result;
    }
}

