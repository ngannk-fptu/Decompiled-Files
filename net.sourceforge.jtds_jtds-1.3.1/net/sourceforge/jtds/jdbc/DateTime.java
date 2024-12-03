/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import net.sourceforge.jtds.jdbc.Messages;

public class DateTime {
    static final int DATE_NOT_USED = Integer.MIN_VALUE;
    static final int TIME_NOT_USED = Integer.MIN_VALUE;
    private int date;
    private int time;
    private short year;
    private short month;
    private short day;
    private short hour;
    private short minute;
    private short second;
    private short millis;
    private boolean unpacked;
    private String stringValue;
    private Timestamp tsValue;
    private Date dateValue;
    private Time timeValue;

    DateTime(int date, int time) {
        this.date = date;
        this.time = time;
    }

    DateTime(short date, short time) {
        this.date = date & 0xFFFF;
        this.time = time * 60 * 300;
    }

    DateTime(Timestamp ts) throws SQLException {
        this.tsValue = ts;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(ts);
        if (cal.get(0) != 1) {
            throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
        }
        this.year = (short)cal.get(1);
        this.month = (short)(cal.get(2) + 1);
        this.day = (short)cal.get(5);
        this.hour = (short)cal.get(11);
        this.minute = (short)cal.get(12);
        this.second = (short)cal.get(13);
        this.millis = (short)cal.get(14);
        this.packDate();
        this.packTime();
        this.unpacked = true;
    }

    DateTime(Time t) throws SQLException {
        this.timeValue = t;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(t);
        if (cal.get(0) != 1) {
            throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
        }
        this.date = Integer.MIN_VALUE;
        this.year = (short)1900;
        this.month = 1;
        this.day = 1;
        this.hour = (short)cal.get(11);
        this.minute = (short)cal.get(12);
        this.second = (short)cal.get(13);
        this.millis = (short)cal.get(14);
        this.packTime();
        this.year = (short)1970;
        this.month = 1;
        this.day = 1;
        this.unpacked = true;
    }

    DateTime(Date d) throws SQLException {
        this.dateValue = d;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        if (cal.get(0) != 1) {
            throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
        }
        this.year = (short)cal.get(1);
        this.month = (short)(cal.get(2) + 1);
        this.day = (short)cal.get(5);
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.millis = 0;
        this.packDate();
        this.time = Integer.MIN_VALUE;
        this.unpacked = true;
    }

    int getDate() {
        return this.date == Integer.MIN_VALUE ? 0 : this.date;
    }

    int getTime() {
        return this.time == Integer.MIN_VALUE ? 0 : this.time;
    }

    private void unpackDateTime() {
        if (this.date == Integer.MIN_VALUE) {
            this.year = (short)1970;
            this.month = 1;
            this.day = 1;
        } else if (this.date == 0) {
            this.year = (short)1900;
            this.month = 1;
            this.day = 1;
        } else {
            int l = this.date + 68569 + 2415021;
            int n = 4 * l / 146097;
            int i = 4000 * ((l -= (146097 * n + 3) / 4) + 1) / 1461001;
            l = l - 1461 * i / 4 + 31;
            int j = 80 * l / 2447;
            int k = l - 2447 * j / 80;
            l = j / 11;
            j = j + 2 - 12 * l;
            i = 100 * (n - 49) + i + l;
            this.year = (short)i;
            this.month = (short)j;
            this.day = (short)k;
        }
        if (this.time == Integer.MIN_VALUE) {
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
        } else {
            int hours = this.time / 1080000;
            this.time -= hours * 1080000;
            int minutes = this.time / 18000;
            this.time -= minutes * 18000;
            int seconds = this.time / 300;
            this.time -= seconds * 300;
            this.time = Math.round((float)(this.time * 1000) / 300.0f);
            this.hour = (short)hours;
            this.minute = (short)minutes;
            this.second = (short)seconds;
            this.millis = (short)this.time;
        }
        this.unpacked = true;
    }

    public void packDate() throws SQLException {
        if (this.year < 1753 || this.year > 9999) {
            throw new SQLException(Messages.get("error.datetime.range"), "22003");
        }
        this.date = this.day - 32075 + 1461 * (this.year + 4800 + (this.month - 14) / 12) / 4 + 367 * (this.month - 2 - (this.month - 14) / 12 * 12) / 12 - 3 * ((this.year + 4900 + (this.month - 14) / 12) / 100) / 4 - 2415021;
    }

    public void packTime() {
        this.time = this.hour * 1080000;
        this.time += this.minute * 18000;
        this.time += this.second * 300;
        this.time += Math.round((float)this.millis * 300.0f / 1000.0f);
        if (this.time > 25919999) {
            this.time = 0;
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
            this.millis = 0;
            if (this.date != Integer.MIN_VALUE) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.set(1, this.year);
                cal.set(2, this.month - 1);
                cal.set(5, this.day);
                cal.add(5, 1);
                this.year = (short)cal.get(1);
                this.month = (short)(cal.get(2) + 1);
                this.day = (short)cal.get(5);
                ++this.date;
            }
        }
    }

    public Timestamp toTimestamp() {
        if (this.tsValue == null) {
            if (!this.unpacked) {
                this.unpackDateTime();
            }
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(1, this.year);
            cal.set(2, this.month - 1);
            cal.set(5, this.day);
            cal.set(11, this.hour);
            cal.set(12, this.minute);
            cal.set(13, this.second);
            cal.set(14, this.millis);
            this.tsValue = new Timestamp(cal.getTime().getTime());
        }
        return this.tsValue;
    }

    public Date toDate() {
        if (this.dateValue == null) {
            if (!this.unpacked) {
                this.unpackDateTime();
            }
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(1, this.year);
            cal.set(2, this.month - 1);
            cal.set(5, this.day);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            this.dateValue = new Date(cal.getTime().getTime());
        }
        return this.dateValue;
    }

    public Time toTime() {
        if (this.timeValue == null) {
            if (!this.unpacked) {
                this.unpackDateTime();
            }
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(1, 1970);
            cal.set(2, 0);
            cal.set(5, 1);
            cal.set(11, this.hour);
            cal.set(12, this.minute);
            cal.set(13, this.second);
            cal.set(14, this.millis);
            this.timeValue = new Time(cal.getTime().getTime());
        }
        return this.timeValue;
    }

    public Object toObject() {
        if (this.date == Integer.MIN_VALUE) {
            return this.toTime();
        }
        if (this.time == Integer.MIN_VALUE) {
            return this.toDate();
        }
        return this.toTimestamp();
    }

    public String toString() {
        if (this.stringValue == null) {
            if (!this.unpacked) {
                this.unpackDateTime();
            }
            int day = this.day;
            int month = this.month;
            int year = this.year;
            int millis = this.millis;
            int second = this.second;
            int minute = this.minute;
            int hour = this.hour;
            char[] buf = new char[23];
            int p = 0;
            if (this.date != Integer.MIN_VALUE) {
                p = 10;
                buf[--p] = (char)(48 + day % 10);
                buf[--p] = (char)(48 + (day /= 10) % 10);
                buf[--p] = 45;
                buf[--p] = (char)(48 + month % 10);
                buf[--p] = (char)(48 + (month /= 10) % 10);
                buf[--p] = 45;
                buf[--p] = (char)(48 + year % 10);
                buf[--p] = (char)(48 + (year /= 10) % 10);
                buf[--p] = (char)(48 + (year /= 10) % 10);
                buf[--p] = (char)(48 + (year /= 10) % 10);
                p += 10;
                if (this.time != Integer.MIN_VALUE) {
                    buf[p++] = 32;
                }
            }
            if (this.time != Integer.MIN_VALUE) {
                p += 12;
                buf[--p] = (char)(48 + millis % 10);
                buf[--p] = (char)(48 + (millis /= 10) % 10);
                buf[--p] = (char)(48 + (millis /= 10) % 10);
                buf[--p] = 46;
                buf[--p] = (char)(48 + second % 10);
                buf[--p] = (char)(48 + (second /= 10) % 10);
                buf[--p] = 58;
                buf[--p] = (char)(48 + minute % 10);
                buf[--p] = (char)(48 + (minute /= 10) % 10);
                buf[--p] = 58;
                buf[--p] = (char)(48 + hour % 10);
                buf[--p] = (char)(48 + (hour /= 10) % 10);
                if (buf[(p += 12) - 1] == '0') {
                    --p;
                }
                if (buf[p - 1] == '0') {
                    --p;
                }
            }
            this.stringValue = String.valueOf(buf, 0, p);
        }
        return this.stringValue;
    }
}

