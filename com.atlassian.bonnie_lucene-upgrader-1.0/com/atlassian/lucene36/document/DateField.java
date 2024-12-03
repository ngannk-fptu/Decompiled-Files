/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import java.util.Date;

@Deprecated
public class DateField {
    private static int DATE_LEN = Long.toString(31536000000000L, 36).length();

    private DateField() {
    }

    public static String MIN_DATE_STRING() {
        return DateField.timeToString(0L);
    }

    public static String MAX_DATE_STRING() {
        char[] buffer = new char[DATE_LEN];
        char c = Character.forDigit(35, 36);
        for (int i = 0; i < DATE_LEN; ++i) {
            buffer[i] = c;
        }
        return new String(buffer);
    }

    public static String dateToString(Date date) {
        return DateField.timeToString(date.getTime());
    }

    public static String timeToString(long time) {
        if (time < 0L) {
            throw new RuntimeException("time '" + time + "' is too early, must be >= 0");
        }
        String s = Long.toString(time, 36);
        if (s.length() > DATE_LEN) {
            throw new RuntimeException("time '" + time + "' is too late, length of string " + "representation must be <= " + DATE_LEN);
        }
        if (s.length() < DATE_LEN) {
            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < DATE_LEN) {
                sb.insert(0, 0);
            }
            s = sb.toString();
        }
        return s;
    }

    public static long stringToTime(String s) {
        return Long.parseLong(s, 36);
    }

    public static Date stringToDate(String s) {
        return new Date(DateField.stringToTime(s));
    }
}

