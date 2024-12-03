/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.util.Calendar;
import org.apache.axis.utils.Messages;

public class Duration
implements Serializable {
    boolean isNegative = false;
    int years;
    int months;
    int days;
    int hours;
    int minutes;
    double seconds;

    public Duration() {
    }

    public Duration(boolean negative, int aYears, int aMonths, int aDays, int aHours, int aMinutes, double aSeconds) {
        this.isNegative = negative;
        this.years = aYears;
        this.months = aMonths;
        this.days = aDays;
        this.hours = aHours;
        this.minutes = aMinutes;
        this.setSeconds(aSeconds);
    }

    public Duration(String duration) throws IllegalArgumentException {
        int position = 1;
        int timePosition = duration.indexOf("T");
        if (duration.indexOf("P") == -1 || duration.equals("P")) {
            throw new IllegalArgumentException(Messages.getMessage("badDuration"));
        }
        if (duration.lastIndexOf("T") == duration.length() - 1) {
            throw new IllegalArgumentException(Messages.getMessage("badDuration"));
        }
        if (duration.startsWith("-")) {
            this.isNegative = true;
            ++position;
        }
        if (timePosition != -1) {
            this.parseTime(duration.substring(timePosition + 1));
        } else {
            timePosition = duration.length();
        }
        if (position != timePosition) {
            this.parseDate(duration.substring(position, timePosition));
        }
    }

    public Duration(boolean negative, Calendar calendar) throws IllegalArgumentException {
        this.isNegative = negative;
        this.years = calendar.get(1);
        this.months = calendar.get(2);
        this.days = calendar.get(5);
        this.hours = calendar.get(10);
        this.minutes = calendar.get(12);
        this.seconds = calendar.get(13);
        this.seconds += (double)calendar.get(14) / 100.0;
        if (this.years == 0 && this.months == 0 && this.days == 0 && this.hours == 0 && this.minutes == 0 && this.seconds == 0.0) {
            throw new IllegalArgumentException(Messages.getMessage("badCalendarForDuration"));
        }
    }

    public void parseTime(String time) throws IllegalArgumentException {
        if (time.length() == 0 || time.indexOf("-") != -1) {
            throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
        }
        if (!(time.endsWith("H") || time.endsWith("M") || time.endsWith("S"))) {
            throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
        }
        try {
            int start = 0;
            int end = time.indexOf("H");
            if (start == end) {
                throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
            }
            if (end != -1) {
                this.hours = Integer.parseInt(time.substring(0, end));
                start = end + 1;
            }
            if (start == (end = time.indexOf("M"))) {
                throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
            }
            if (end != -1) {
                this.minutes = Integer.parseInt(time.substring(start, end));
                start = end + 1;
            }
            if (start == (end = time.indexOf("S"))) {
                throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
            }
            if (end != -1) {
                this.setSeconds(Double.parseDouble(time.substring(start, end)));
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
        }
    }

    public void parseDate(String date) throws IllegalArgumentException {
        if (date.length() == 0 || date.indexOf("-") != -1) {
            throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
        }
        if (!(date.endsWith("Y") || date.endsWith("M") || date.endsWith("D"))) {
            throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
        }
        try {
            int start = 0;
            int end = date.indexOf("Y");
            if (start == end) {
                throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
            }
            if (end != -1) {
                this.years = Integer.parseInt(date.substring(0, end));
                start = end + 1;
            }
            if (start == (end = date.indexOf("M"))) {
                throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
            }
            if (end != -1) {
                this.months = Integer.parseInt(date.substring(start, end));
                start = end + 1;
            }
            if (start == (end = date.indexOf("D"))) {
                throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
            }
            if (end != -1) {
                this.days = Integer.parseInt(date.substring(start, end));
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
        }
    }

    public boolean isNegative() {
        return this.isNegative;
    }

    public int getYears() {
        return this.years;
    }

    public int getMonths() {
        return this.months;
    }

    public int getDays() {
        return this.days;
    }

    public int getHours() {
        return this.hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public double getSeconds() {
        return this.seconds;
    }

    public void setNegative(boolean negative) {
        this.isNegative = negative;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = (double)Math.round(seconds * 100.0) / 100.0;
    }

    public String toString() {
        StringBuffer duration = new StringBuffer();
        duration.append("P");
        if (this.years != 0) {
            duration.append(this.years + "Y");
        }
        if (this.months != 0) {
            duration.append(this.months + "M");
        }
        if (this.days != 0) {
            duration.append(this.days + "D");
        }
        if (this.hours != 0 || this.minutes != 0 || this.seconds != 0.0) {
            duration.append("T");
            if (this.hours != 0) {
                duration.append(this.hours + "H");
            }
            if (this.minutes != 0) {
                duration.append(this.minutes + "M");
            }
            if (this.seconds != 0.0) {
                if (this.seconds == (double)((int)this.seconds)) {
                    duration.append((int)this.seconds + "S");
                } else {
                    duration.append(this.seconds + "S");
                }
            }
        }
        if (duration.length() == 1) {
            duration.append("T0S");
        }
        if (this.isNegative) {
            duration.insert(0, "-");
        }
        return duration.toString();
    }

    public boolean equals(Object object) {
        if (!(object instanceof Duration)) {
            return false;
        }
        Calendar thisCalendar = this.getAsCalendar();
        Duration duration = (Duration)object;
        return this.isNegative == duration.isNegative && this.getAsCalendar().equals(duration.getAsCalendar());
    }

    public int hashCode() {
        int hashCode = 0;
        if (this.isNegative) {
            ++hashCode;
        }
        hashCode += this.years;
        hashCode += this.months;
        hashCode += this.days;
        hashCode += this.hours;
        hashCode += this.minutes;
        hashCode = (int)((double)hashCode + this.seconds);
        hashCode = (int)((double)hashCode + this.seconds * 100.0 % 100.0);
        return hashCode;
    }

    public Calendar getAsCalendar() {
        return this.getAsCalendar(Calendar.getInstance());
    }

    public Calendar getAsCalendar(Calendar startTime) {
        Calendar ret = (Calendar)startTime.clone();
        ret.set(1, this.years);
        ret.set(2, this.months);
        ret.set(5, this.days);
        ret.set(10, this.hours);
        ret.set(12, this.minutes);
        ret.set(13, (int)this.seconds);
        ret.set(14, (int)(this.seconds * 100.0 - (double)(Math.round(this.seconds) * 100L)));
        return ret;
    }
}

