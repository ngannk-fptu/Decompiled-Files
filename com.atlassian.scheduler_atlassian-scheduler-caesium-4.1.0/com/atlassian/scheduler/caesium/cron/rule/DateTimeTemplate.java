/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 *  org.joda.time.ReadableDateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.caesium.cron.rule;

import java.util.Date;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.ReadableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeTemplate
implements Cloneable {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeTemplate.class);
    private static final int[] MAX_DAY_BY_MONTH = new int[]{-1, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final DateTimeZone zone;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public DateTimeTemplate(ReadableDateTime dateTime) {
        this.zone = dateTime.getZone();
        this.year = dateTime.getYear();
        this.month = dateTime.getMonthOfYear();
        this.day = dateTime.getDayOfMonth();
        this.hour = dateTime.getHourOfDay();
        this.minute = dateTime.getMinuteOfHour();
        this.second = dateTime.getSecondOfMinute();
    }

    public DateTimeTemplate(Date date, DateTimeZone zone) {
        this((ReadableDateTime)new DateTime(date.getTime(), zone));
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public DateTimeZone getZone() {
        return this.zone;
    }

    public LocalDate toFirstOfMonth() {
        return new LocalDate(this.year, this.month, 1);
    }

    @Nullable
    public DateTime toDateTime() {
        if (!this.isPlausible()) {
            return null;
        }
        try {
            long later;
            long earlier;
            DateTime dateTime = new DateTime(this.year, this.month, this.day, this.hour, this.minute, this.second, 0, this.zone);
            if (!this.zone.isFixed() && (earlier = dateTime.getMillis()) != (later = this.zone.adjustOffset(earlier, true))) {
                return new DateTime(later, this.zone);
            }
            return dateTime;
        }
        catch (IllegalArgumentException iae) {
            LOG.debug("Invalid date: {}", (Object)this, (Object)iae);
            return null;
        }
    }

    private boolean isPlausible() {
        if (this.month < 1 || this.month > 12 || this.day > MAX_DAY_BY_MONTH[this.month]) {
            return false;
        }
        return this.day != 29 || this.month != 2 || (this.year & 3) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64).append(this.year);
        DateTimeTemplate.append2(sb, '/', this.month);
        DateTimeTemplate.append2(sb, '/', this.day);
        DateTimeTemplate.append2(sb, ' ', this.hour);
        DateTimeTemplate.append2(sb, ':', this.minute);
        DateTimeTemplate.append2(sb, ':', this.second);
        return sb.append(" (").append(this.zone).append(')').toString();
    }

    private static void append2(StringBuilder sb, char delimiter, int value) {
        sb.append(delimiter);
        if (value < 10) {
            sb.append('0');
        }
        sb.append(value);
    }

    public static enum Field {
        YEAR(1970, 2999){

            @Override
            public int get(DateTimeTemplate dateTime) {
                return dateTime.getYear();
            }

            @Override
            public void set(DateTimeTemplate dateTime, int value) {
                dateTime.setYear(value);
            }
        }
        ,
        MONTH(1, 12){

            @Override
            public int get(DateTimeTemplate dateTime) {
                return dateTime.getMonth();
            }

            @Override
            public void set(DateTimeTemplate dateTime, int value) {
                dateTime.setMonth(value);
            }
        }
        ,
        DAY(1, 31){

            @Override
            public int get(DateTimeTemplate dateTime) {
                return dateTime.getDay();
            }

            @Override
            public void set(DateTimeTemplate dateTime, int value) {
                dateTime.setDay(value);
            }
        }
        ,
        HOUR(0, 23){

            @Override
            public int get(DateTimeTemplate dateTime) {
                return dateTime.getHour();
            }

            @Override
            public void set(DateTimeTemplate dateTime, int value) {
                dateTime.setHour(value);
            }
        }
        ,
        MINUTE(0, 59){

            @Override
            public int get(DateTimeTemplate dateTime) {
                return dateTime.getMinute();
            }

            @Override
            public void set(DateTimeTemplate dateTime, int value) {
                dateTime.setMinute(value);
            }
        }
        ,
        SECOND(0, 59){

            @Override
            public int get(DateTimeTemplate dateTime) {
                return dateTime.getSecond();
            }

            @Override
            public void set(DateTimeTemplate dateTime, int value) {
                dateTime.setSecond(value);
            }
        };

        private final int minimumValue;
        private final int maximumValue;

        private Field(int minimumValue, int maximumValue) {
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        public abstract int get(DateTimeTemplate var1);

        public abstract void set(DateTimeTemplate var1, int var2);

        public int getMinimumValue() {
            return this.minimumValue;
        }

        public int getMaximumValue() {
            return this.maximumValue;
        }
    }
}

