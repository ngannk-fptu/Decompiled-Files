/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.TimeZones;

public final class Dates {
    public static final long MILLIS_PER_SECOND = 1000L;
    public static final long MILLIS_PER_MINUTE = 60000L;
    public static final long MILLIS_PER_HOUR = 3600000L;
    public static final long MILLIS_PER_DAY = 86400000L;
    public static final long MILLIS_PER_WEEK = 604800000L;
    public static final int DAYS_PER_WEEK = 7;
    public static final int PRECISION_SECOND = 0;
    public static final int PRECISION_DAY = 1;
    public static final int MAX_WEEKS_PER_YEAR = 53;
    public static final int MAX_DAYS_PER_YEAR = 366;
    public static final int MAX_DAYS_PER_MONTH = 31;
    private static final String INVALID_WEEK_MESSAGE = "Invalid week number [{0}]";
    private static final String INVALID_YEAR_DAY_MESSAGE = "Invalid year day [{0}]";
    private static final String INVALID_MONTH_DAY_MESSAGE = "Invalid month day [{0}]";

    private Dates() {
    }

    public static int getAbsWeekNo(Date date, int weekNo) {
        if (weekNo == 0 || weekNo < -53 || weekNo > 53) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_WEEK_MESSAGE, weekNo));
        }
        if (weekNo > 0) {
            return weekNo;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(1);
        ArrayList<Integer> weeks = new ArrayList<Integer>();
        cal.set(3, 1);
        while (cal.get(1) == year) {
            weeks.add(cal.get(3));
            cal.add(3, 1);
        }
        return (Integer)weeks.get(weeks.size() + weekNo);
    }

    public static int getAbsYearDay(Date date, int yearDay) {
        if (yearDay == 0 || yearDay < -366 || yearDay > 366) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_YEAR_DAY_MESSAGE, yearDay));
        }
        if (yearDay > 0) {
            return yearDay;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(1);
        ArrayList<Integer> days = new ArrayList<Integer>();
        cal.set(6, 1);
        while (cal.get(1) == year) {
            days.add(cal.get(6));
            cal.add(6, 1);
        }
        return (Integer)days.get(days.size() + yearDay);
    }

    public static int getAbsMonthDay(Date date, int monthDay) {
        if (monthDay == 0 || monthDay < -31 || monthDay > 31) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_MONTH_DAY_MESSAGE, monthDay));
        }
        if (monthDay > 0) {
            return monthDay;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(2);
        ArrayList<Integer> days = new ArrayList<Integer>();
        cal.set(5, 1);
        while (cal.get(2) == month) {
            days.add(cal.get(5));
            cal.add(5, 1);
        }
        return (Integer)days.get(days.size() + monthDay);
    }

    public static net.fortuna.ical4j.model.Date getInstance(Date date, Value type) {
        if (Value.DATE.equals(type)) {
            return new net.fortuna.ical4j.model.Date(date);
        }
        return new DateTime(date);
    }

    public static Calendar getCalendarInstance(net.fortuna.ical4j.model.Date date) {
        DateTime dateTime;
        Calendar instance = date instanceof DateTime ? ((dateTime = (DateTime)date).getTimeZone() != null ? Calendar.getInstance(dateTime.getTimeZone()) : (dateTime.isUtc() ? Calendar.getInstance(TimeZones.getUtcTimeZone()) : Calendar.getInstance())) : Calendar.getInstance(TimeZones.getDateTimeZone());
        return instance;
    }

    public static DateList getDateListInstance(DateList origList) {
        DateList list = new DateList(origList.getType());
        if (origList.isUtc()) {
            list.setUtc(true);
        } else {
            list.setTimeZone(origList.getTimeZone());
        }
        return list;
    }

    public static long round(long time, int precision) {
        return Dates.round(time, precision, TimeZone.getDefault());
    }

    public static long round(long time, int precision, TimeZone tz) {
        if (precision == 0 && time % 1000L == 0L) {
            return time;
        }
        Calendar cal = Calendar.getInstance(tz);
        cal.setTimeInMillis(time);
        if (precision == 1) {
            cal.set(11, 0);
            cal.clear(12);
            cal.clear(13);
            cal.clear(14);
        } else if (precision == 0) {
            cal.clear(14);
        }
        return cal.getTimeInMillis();
    }

    public static long getCurrentTimeRounded() {
        return (long)Math.floor((double)System.currentTimeMillis() / 1000.0) * 1000L;
    }
}

