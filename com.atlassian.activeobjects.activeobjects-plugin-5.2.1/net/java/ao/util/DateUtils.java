/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import net.java.ao.ActiveObjectsException;

public final class DateUtils {
    public static final Date MAX_DATE = DateUtils.newDate(9999, 12, 31);

    public static DateFormat newDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static Calendar checkAgainstMaxDate(Calendar c) {
        DateUtils.checkAgainstMaxDate(c.getTime());
        return c;
    }

    public static Date checkAgainstMaxDate(Date date) {
        if (date.compareTo(MAX_DATE) > 0) {
            throw new ActiveObjectsException("Default date value must be strictly before " + MAX_DATE);
        }
        return date;
    }

    private static Date newDate(int year, int month, int dayOfMonth) {
        return DateUtils.newCalendar(year, month, dayOfMonth).getTime();
    }

    private static Calendar newCalendar(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, dayOfMonth);
        c.set(14, 0);
        return c;
    }
}

