/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.TimeFormat;

public class Time {
    private int time = -1;
    public static final int SECONDS_IN_MINUTE = 60;

    public Time() {
        this(0);
    }

    public Time(int n) {
        this.setTime(n);
    }

    public void setTime(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Time argument must be 0 or positive!");
        }
        this.time = n;
    }

    public int getTime() {
        return this.time;
    }

    public long getTimeInMillis() {
        return (long)this.time * 1000L;
    }

    public void setSeconds(int n) {
        this.time = this.getMinutes() * 60 + n;
    }

    public int getSeconds() {
        return this.time % 60;
    }

    public void setMinutes(int n) {
        this.time = n * 60 + this.getSeconds();
    }

    public int getMinutes() {
        return this.time / 60;
    }

    public String toString() {
        return this.getMinutes() + ":" + (this.getSeconds() < 10 ? "0" : "") + this.getSeconds();
    }

    @Deprecated
    public String toString(String string) {
        TimeFormat timeFormat = new TimeFormat(string);
        return timeFormat.format(this);
    }

    @Deprecated
    public static Time parseTime(String string) {
        TimeFormat timeFormat = TimeFormat.getInstance();
        return timeFormat.parse(string);
    }
}

