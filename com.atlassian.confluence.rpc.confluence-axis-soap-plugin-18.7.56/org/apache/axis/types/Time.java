/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.axis.utils.Messages;

public class Time
implements Serializable {
    private Calendar _value;
    private static SimpleDateFormat zulu = new SimpleDateFormat("HH:mm:ss.SSS'Z'");

    public Time(Calendar value) {
        this._value = value;
        this._value.set(0, 0, 0);
    }

    public Time(String value) throws NumberFormatException {
        this._value = this.makeValue(value);
    }

    public Calendar getAsCalendar() {
        return this._value;
    }

    public void setTime(Calendar date) {
        this._value = date;
        this._value.set(0, 0, 0);
    }

    public void setTime(Date date) {
        this._value.setTime(date);
        this._value.set(0, 0, 0);
    }

    private Calendar makeValue(String source) throws NumberFormatException {
        Calendar calendar = Calendar.getInstance();
        this.validateSource(source);
        Date date = Time.ParseHoursMinutesSeconds(source);
        int pos = 8;
        if (source != null) {
            if (pos < source.length() && source.charAt(pos) == '.') {
                int milliseconds = 0;
                int start = ++pos;
                while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                    ++pos;
                }
                String decimal = source.substring(start, pos);
                if (decimal.length() == 3) {
                    milliseconds = Integer.parseInt(decimal);
                } else if (decimal.length() < 3) {
                    milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
                } else {
                    milliseconds = Integer.parseInt(decimal.substring(0, 3));
                    if (decimal.charAt(3) >= '5') {
                        ++milliseconds;
                    }
                }
                date.setTime(date.getTime() + (long)milliseconds);
            }
            if (pos + 5 < source.length() && (source.charAt(pos) == '+' || source.charAt(pos) == '-')) {
                if (!(Character.isDigit(source.charAt(pos + 1)) && Character.isDigit(source.charAt(pos + 2)) && source.charAt(pos + 3) == ':' && Character.isDigit(source.charAt(pos + 4)) && Character.isDigit(source.charAt(pos + 5)))) {
                    throw new NumberFormatException(Messages.getMessage("badTimezone00"));
                }
                int hours = (source.charAt(pos + 1) - 48) * 10 + source.charAt(pos + 2) - 48;
                int mins = (source.charAt(pos + 4) - 48) * 10 + source.charAt(pos + 5) - 48;
                int milliseconds = (hours * 60 + mins) * 60 * 1000;
                if (source.charAt(pos) == '+') {
                    milliseconds = -milliseconds;
                }
                date.setTime(date.getTime() + (long)milliseconds);
                pos += 6;
            }
            if (pos < source.length() && source.charAt(pos) == 'Z') {
                ++pos;
                calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
            if (pos < source.length()) {
                throw new NumberFormatException(Messages.getMessage("badChars00"));
            }
        }
        calendar.setTime(date);
        calendar.set(0, 0, 0);
        return calendar;
    }

    private int getTimezoneNumberValue(char c) {
        int n = c - 48;
        if (n < 0 || n > 9) {
            throw new NumberFormatException(Messages.getMessage("badTimezone00"));
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Date ParseHoursMinutesSeconds(String source) {
        Date date;
        try {
            SimpleDateFormat simpleDateFormat = zulu;
            synchronized (simpleDateFormat) {
                String fulltime = source == null ? null : source.substring(0, 8) + ".000Z";
                date = zulu.parse(fulltime);
            }
        }
        catch (Exception e) {
            throw new NumberFormatException(e.toString());
        }
        return date;
    }

    private void validateSource(String source) {
        if (source != null) {
            if (source.charAt(2) != ':' || source.charAt(5) != ':') {
                throw new NumberFormatException(Messages.getMessage("badTime00"));
            }
            if (source.length() < 8) {
                throw new NumberFormatException(Messages.getMessage("badTime00"));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        if (this._value == null) {
            return "unassigned Time";
        }
        SimpleDateFormat simpleDateFormat = zulu;
        synchronized (simpleDateFormat) {
            return zulu.format(this._value.getTime());
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Time)) {
            return false;
        }
        Time other = (Time)obj;
        if (this == obj) {
            return true;
        }
        boolean _equals = this._value == null && other._value == null || this._value != null && this._value.getTime().equals(other._value.getTime());
        return _equals;
    }

    public int hashCode() {
        return this._value == null ? 0 : this._value.hashCode();
    }

    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}

