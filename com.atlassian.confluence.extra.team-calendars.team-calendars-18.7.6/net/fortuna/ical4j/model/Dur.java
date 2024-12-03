/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.StringTokenizer;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.util.Dates;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Deprecated
public class Dur
implements Comparable<Dur>,
Serializable {
    private static final long serialVersionUID = 5013232281547134583L;
    private static final int DAYS_PER_WEEK = 7;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int DAYS_PER_YEAR = 365;
    private boolean negative;
    private int weeks;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    public Dur(String value) {
        this.negative = false;
        this.weeks = 0;
        this.days = 0;
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
        String token = null;
        StringTokenizer t = new StringTokenizer(value, "+-PWDTHMS", true);
        while (t.hasMoreTokens()) {
            String prevToken = token;
            token = t.nextToken();
            if ("+".equals(token)) {
                this.negative = false;
                continue;
            }
            if ("-".equals(token)) {
                this.negative = true;
                continue;
            }
            if ("W".equals(token)) {
                this.weeks = Integer.parseInt(prevToken);
                continue;
            }
            if ("D".equals(token)) {
                this.days = Integer.parseInt(prevToken);
                continue;
            }
            if ("H".equals(token)) {
                this.hours = Integer.parseInt(prevToken);
                continue;
            }
            if ("M".equals(token)) {
                this.minutes = Integer.parseInt(prevToken);
                continue;
            }
            if (!"S".equals(token)) continue;
            this.seconds = Integer.parseInt(prevToken);
        }
    }

    public Dur(int weeks) {
        this.weeks = Math.abs(weeks);
        this.days = 0;
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
        this.negative = weeks < 0;
    }

    public Dur(int days, int hours, int minutes, int seconds) {
        if (!(days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0 || days <= 0 && hours <= 0 && minutes <= 0 && seconds <= 0)) {
            throw new IllegalArgumentException("Invalid duration representation");
        }
        this.weeks = 0;
        this.days = Math.abs(days);
        this.hours = Math.abs(hours);
        this.minutes = Math.abs(minutes);
        this.seconds = Math.abs(seconds);
        this.negative = days < 0 || hours < 0 || minutes < 0 || seconds < 0;
    }

    public Dur(java.util.Date date1, java.util.Date date2) {
        java.util.Date end;
        java.util.Date start;
        boolean bl = this.negative = date1.compareTo(date2) > 0;
        if (this.negative) {
            start = date2;
            end = date1;
        } else {
            start = date1;
            end = date2;
        }
        Calendar startCal = start instanceof Date ? Dates.getCalendarInstance((Date)start) : Calendar.getInstance();
        startCal.setTime(start);
        Calendar endCal = Calendar.getInstance(startCal.getTimeZone());
        endCal.setTime(end);
        long dur = 0L;
        int nYears = endCal.get(1) - startCal.get(1);
        while (nYears > 0) {
            startCal.add(5, 365 * nYears);
            dur += (long)(365 * nYears);
            nYears = endCal.get(1) - startCal.get(1);
        }
        dur += (long)(endCal.get(6) - startCal.get(6));
        dur *= 24L;
        dur += (long)(endCal.get(11) - startCal.get(11));
        dur *= 60L;
        dur += (long)(endCal.get(12) - startCal.get(12));
        dur *= 60L;
        this.seconds = (int)((dur += (long)(endCal.get(13) - startCal.get(13))) % 60L);
        this.minutes = (int)((dur /= 60L) % 60L);
        this.hours = (int)((dur /= 60L) % 24L);
        this.days = (int)(dur /= 24L);
        this.weeks = 0;
        if (this.seconds == 0 && this.minutes == 0 && this.hours == 0 && this.days % 7 == 0) {
            this.weeks = this.days / 7;
            this.days = 0;
        }
    }

    public final java.util.Date getTime(java.util.Date start) {
        Calendar cal = start instanceof Date ? Dates.getCalendarInstance((Date)start) : Calendar.getInstance();
        cal.setTime(start);
        if (this.isNegative()) {
            cal.add(3, -this.weeks);
            cal.add(7, -this.days);
            cal.add(11, -this.hours);
            cal.add(12, -this.minutes);
            cal.add(13, -this.seconds);
        } else {
            cal.add(3, this.weeks);
            cal.add(7, this.days);
            cal.add(11, this.hours);
            cal.add(12, this.minutes);
            cal.add(13, this.seconds);
        }
        return cal.getTime();
    }

    public final Dur negate() {
        Dur negated = new Dur(this.days, this.hours, this.minutes, this.seconds);
        negated.weeks = this.weeks;
        negated.negative = !this.negative;
        return negated;
    }

    public final Dur add(Dur duration) {
        Dur sum;
        if (!this.isNegative() && duration.isNegative() || this.isNegative() && !duration.isNegative()) {
            throw new IllegalArgumentException("Cannot add a negative and a positive duration");
        }
        if (this.weeks > 0 && duration.weeks > 0) {
            sum = new Dur(this.weeks + duration.weeks);
        } else {
            int daySum = this.weeks > 0 ? this.weeks * 7 + this.days : this.days;
            int hourSum = this.hours;
            int minuteSum = this.minutes;
            int secondSum = this.seconds;
            if ((secondSum + duration.seconds) / 60 > 0) {
                minuteSum += (secondSum + duration.seconds) / 60;
                secondSum = (secondSum + duration.seconds) % 60;
            } else {
                secondSum += duration.seconds;
            }
            if ((minuteSum + duration.minutes) / 60 > 0) {
                hourSum += (minuteSum + duration.minutes) / 60;
                minuteSum = (minuteSum + duration.minutes) % 60;
            } else {
                minuteSum += duration.minutes;
            }
            if ((hourSum + duration.hours) / 24 > 0) {
                daySum += (hourSum + duration.hours) / 24;
                hourSum = (hourSum + duration.hours) % 24;
            } else {
                hourSum += duration.hours;
            }
            sum = new Dur(daySum += duration.weeks > 0 ? duration.weeks * 7 + duration.days : duration.days, hourSum, minuteSum, secondSum);
        }
        sum.negative = this.negative;
        return sum;
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        if (this.negative) {
            b.append('-');
        }
        b.append('P');
        if (this.weeks > 0) {
            b.append(this.weeks);
            b.append('W');
        } else {
            if (this.days > 0) {
                b.append(this.days);
                b.append('D');
            }
            if (this.hours > 0 || this.minutes > 0 || this.seconds > 0) {
                b.append('T');
                if (this.hours > 0) {
                    b.append(this.hours);
                    b.append('H');
                }
                if (this.minutes > 0) {
                    b.append(this.minutes);
                    b.append('M');
                }
                if (this.seconds > 0) {
                    b.append(this.seconds);
                    b.append('S');
                }
            }
            if (this.hours + this.minutes + this.seconds + this.days + this.weeks == 0) {
                b.append("T0S");
            }
        }
        return b.toString();
    }

    @Override
    public final int compareTo(Dur arg0) {
        if (this.isNegative() != arg0.isNegative()) {
            if (this.isNegative()) {
                return Integer.MIN_VALUE;
            }
            return Integer.MAX_VALUE;
        }
        int result = this.getWeeks() != arg0.getWeeks() ? this.getWeeks() - arg0.getWeeks() : (this.getDays() != arg0.getDays() ? this.getDays() - arg0.getDays() : (this.getHours() != arg0.getHours() ? this.getHours() - arg0.getHours() : (this.getMinutes() != arg0.getMinutes() ? this.getMinutes() - arg0.getMinutes() : this.getSeconds() - arg0.getSeconds())));
        if (this.isNegative()) {
            return -result;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Dur) {
            return ((Dur)obj).compareTo(this) == 0;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return new HashCodeBuilder().append(this.weeks).append(this.days).append(this.hours).append(this.minutes).append(this.seconds).append(this.negative).toHashCode();
    }

    public final int getDays() {
        return this.days;
    }

    public final int getHours() {
        return this.hours;
    }

    public final int getMinutes() {
        return this.minutes;
    }

    public final boolean isNegative() {
        return this.negative;
    }

    public final int getSeconds() {
        return this.seconds;
    }

    public final int getWeeks() {
        return this.weeks;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}

