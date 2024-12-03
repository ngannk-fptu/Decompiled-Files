/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util.date;

import com.nimbusds.oauth2.sdk.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDate {
    private final int year;
    private final int month;
    private final int day;

    public SimpleDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public String toISO8601String() {
        return this.getYear() + "-" + (this.getMonth() < 10 ? "0" : "") + this.getMonth() + "-" + (this.getDay() < 10 ? "0" : "") + this.getDay();
    }

    public String toString() {
        return this.toISO8601String();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleDate)) {
            return false;
        }
        SimpleDate that = (SimpleDate)o;
        return this.getYear() == that.getYear() && this.getMonth() == that.getMonth() && this.getDay() == that.getDay();
    }

    public int hashCode() {
        return Objects.hash(this.getYear(), this.getMonth(), this.getDay());
    }

    public static SimpleDate parseISO8601String(String s) throws ParseException {
        Pattern p = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher m = p.matcher(s);
        if (!m.matches()) {
            throw new ParseException("Invalid ISO 8601 date: YYYY-MM-DD");
        }
        return new SimpleDate(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
    }
}

