/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
    public static final String ISO_DATE_FORMAT = "yyyyMMdd";
    public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_TIME_FORMAT = "HHmmssSSSzzz";
    public static final String ISO_EXPANDED_TIME_FORMAT = "HH:mm:ss,SSSzzz";
    public static final String ISO_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmssSSSzzz";
    public static final String ISO_EXPANDED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss,SSSzzz";
    public static final DateFormatSymbols dateFormatSymbles;
    private static final String[][] foo;
    private static final int JAN_1_1_JULIAN_DAY = 1721426;
    private static final int EPOCH_JULIAN_DAY = 2440588;
    private static final int EPOCH_YEAR = 1970;
    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60000;
    private static final int ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    public static final boolean isLeapYear(String isoString, boolean expanded) throws ParseException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(DateUtil.isoToDate(isoString, expanded));
        return cal.isLeapYear(cal.get(1));
    }

    public static final boolean isLeapYear(String isoString) throws ParseException {
        return DateUtil.isLeapYear(isoString, false);
    }

    public static final TimeZone getTimeZoneFromDateTime(String date, boolean expanded) throws ParseException {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_DATE_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        formatter.parse(date);
        return formatter.getTimeZone();
    }

    public static final TimeZone getTimeZoneFromDateTime(String date) throws ParseException {
        return DateUtil.getTimeZoneFromDateTime(date, false);
    }

    public static final String add(String isoString, int field, int amount, boolean expanded) throws ParseException {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(DateUtil.isoToDate(isoString, expanded));
        cal.add(field, amount);
        return DateUtil.dateToISO(cal.getTime(), expanded);
    }

    public static final String add(String isoString, int field, int amount) throws ParseException {
        return DateUtil.add(isoString, field, amount, false);
    }

    public static final String dateToISO(Date date, boolean expanded) {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_DATE_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    public static final String dateToISO(Date date) {
        return DateUtil.dateToISO(date, false);
    }

    public static final long dateToJulianDay(Date date) {
        return DateUtil.millisToJulianDay(date.getTime());
    }

    public static final int daysBetween(Date early, Date late) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(early);
        c2.setTime(late);
        return DateUtil.daysBetween(c1, c2);
    }

    public static final int daysBetween(Calendar early, Calendar late) {
        return (int)(DateUtil.toJulian(late) - DateUtil.toJulian(early));
    }

    public static final long daysBetween(String isoEarly, String isoLate, boolean expanded) throws ParseException {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeZone(DateUtil.getTimeZoneFromDateTime(isoEarly, expanded));
        c1.setTime(DateUtil.isoToDate(isoEarly, expanded));
        c2.setTimeZone(DateUtil.getTimeZoneFromDateTime(isoLate, expanded));
        c2.setTime(DateUtil.isoToDate(isoLate, expanded));
        return DateUtil.millisToJulianDay(c2.getTime().getTime()) - DateUtil.millisToJulianDay(c1.getTime().getTime());
    }

    public static final Date isoToDate(String dateString, boolean expanded) throws ParseException {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_DATE_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new Date(formatter.parse(dateString).getTime());
    }

    public static final Date isoToDate(String dateString) throws ParseException {
        return DateUtil.isoToDate(dateString, false);
    }

    public static final java.sql.Date isoToSQLDate(String dateString, boolean expanded) throws ParseException {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_DATE_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new java.sql.Date(formatter.parse(dateString).getTime());
    }

    public static final java.sql.Date isoToSQLDate(String dateString) throws ParseException {
        return DateUtil.isoToSQLDate(dateString, false);
    }

    public static final Time isoToTime(String dateString, boolean expanded) throws ParseException {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_TIME_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_TIME_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new Time(formatter.parse(dateString).getTime());
    }

    public static final Time isoToTime(String dateString) throws ParseException {
        return DateUtil.isoToTime(dateString, false);
    }

    public static final Timestamp isoToTimestamp(String dateString, boolean expanded) throws ParseException {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_DATE_TIME_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_DATE_TIME_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new Timestamp(formatter.parse(dateString).getTime());
    }

    public static final Timestamp isoToTimestamp(String dateString) throws ParseException {
        return DateUtil.isoToTimestamp(dateString, false);
    }

    public static final java.sql.Date julianDayCountToDate(long julian) {
        return new java.sql.Date(DateUtil.julianDayToMillis(julian));
    }

    public static final Date julianDayToDate(long julian) {
        return new Date(DateUtil.julianDayToMillis(julian));
    }

    public static final long julianDayToMillis(long julian) {
        return (julian - 2440588L + 1721426L) * 86400000L;
    }

    public static final long millisToJulianDay(long millis) {
        return 719162L + millis / 86400000L;
    }

    public static final String roll(String isoString, int field, boolean up, boolean expanded) throws ParseException {
        Calendar cal = GregorianCalendar.getInstance(DateUtil.getTimeZoneFromDateTime(isoString, expanded));
        cal.setTime(DateUtil.isoToDate(isoString, expanded));
        cal.roll(field, up);
        return DateUtil.dateToISO(cal.getTime(), expanded);
    }

    public static final String roll(String isoString, int field, boolean up) throws ParseException {
        return DateUtil.roll(isoString, field, up, false);
    }

    public static final String timeToISO(Time date, boolean expanded) {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_TIME_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_TIME_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    public static final String timeToISO(Time date) {
        return DateUtil.timeToISO(date, false);
    }

    public static final String timestampToISO(Timestamp date, boolean expanded) {
        SimpleDateFormat formatter = expanded ? new SimpleDateFormat(ISO_EXPANDED_DATE_TIME_FORMAT, dateFormatSymbles) : new SimpleDateFormat(ISO_DATE_TIME_FORMAT, dateFormatSymbles);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    public static final String timestampToISO(Timestamp date) {
        return DateUtil.timestampToISO(date, false);
    }

    public static final Date toDate(float JD) {
        float Z = DateUtil.normalizedJulian(JD) + 0.5f;
        float W = (int)((Z - 1867216.2f) / 36524.25f);
        float X = (int)(W / 4.0f);
        float A = Z + 1.0f + W - X;
        float B = A + 1524.0f;
        float C = (int)(((double)B - 122.1) / 365.25);
        float D = (int)(365.25f * C);
        float E = (int)((double)(B - D) / 30.6001);
        float F = (int)(30.6001f * E);
        int day = (int)(B - D - F);
        int month = (int)(E - 1.0f);
        if (month > 12) {
            month -= 12;
        }
        int year = (int)(C - 4715.0f);
        if (month > 2) {
            --year;
        }
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, day);
        return c.getTime();
    }

    public static final float toJulian(Calendar c) {
        int Y = c.get(1);
        int M = c.get(2);
        int D = c.get(5);
        int A = Y / 100;
        int B = A / 4;
        int C = 2 - A + B;
        float E = (int)(365.25f * (float)(Y + 4716));
        float F = (int)(30.6001f * (float)(M + 1));
        float JD = (float)(C + D) + E + F - 1524.5f;
        return JD;
    }

    public static final float toJulian(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return DateUtil.toJulian(c);
    }

    protected static final float normalizedJulian(float JD) {
        float f = (float)Math.round(JD + 0.5f) - 0.5f;
        return f;
    }

    private static final long floorDivide(long numerator, long denominator) {
        return numerator >= 0L ? numerator / denominator : (numerator + 1L) / denominator - 1L;
    }

    static {
        foo = new String[0][];
        dateFormatSymbles = new DateFormatSymbols();
        dateFormatSymbles.setZoneStrings(foo);
    }
}

