/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.apache.pdfbox.cos.COSString;

public final class DateConverter {
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLIS_PER_MINUTE = 60000;
    private static final int MILLIS_PER_HOUR = 3600000;
    private static final int HALF_DAY = 43200000;
    private static final int DAY = 86400000;
    private static final String[] ALPHA_START_FORMATS = new String[]{"EEEE, dd MMM yy hh:mm:ss a", "EEEE, MMM dd, yy hh:mm:ss a", "EEEE, MMM dd, yy 'at' hh:mma", "EEEE, MMM dd, yy", "EEEE MMM dd, yy HH:mm:ss", "EEEE MMM dd HH:mm:ss z yy", "EEEE MMM dd HH:mm:ss yy"};
    private static final String[] DIGIT_START_FORMATS = new String[]{"dd MMM yy HH:mm:ss", "dd MMM yy HH:mm", "yyyy MMM d", "yyyymmddhh:mm:ss", "H:m M/d/yy", "M/d/yy HH:mm:ss", "M/d/yy HH:mm", "M/d/yy"};

    private DateConverter() {
    }

    public static String toString(Calendar cal) {
        if (cal == null) {
            return null;
        }
        String offset = DateConverter.formatTZoffset(cal.get(15) + cal.get(16), "'");
        return String.format(Locale.US, "D:%1$4tY%1$2tm%1$2td%1$2tH%1$2tM%1$2tS%2$s'", cal, offset);
    }

    public static String toISO8601(Calendar cal) {
        String offset = DateConverter.formatTZoffset(cal.get(15) + cal.get(16), ":");
        return String.format(Locale.US, "%1$4tY-%1$2tm-%1$2tdT%1$2tH:%1$2tM:%1$2tS%2$s", cal, offset);
    }

    private static int restrainTZoffset(long proposedOffset) {
        if (proposedOffset <= 50400000L && proposedOffset >= -50400000L) {
            return (int)proposedOffset;
        }
        if ((proposedOffset = ((proposedOffset + 43200000L) % 86400000L + 86400000L) % 86400000L) == 0L) {
            return 43200000;
        }
        proposedOffset = (proposedOffset - 43200000L) % 43200000L;
        return (int)proposedOffset;
    }

    static String formatTZoffset(long millis, String sep) {
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        sdf.setTimeZone(new SimpleTimeZone(DateConverter.restrainTZoffset(millis), "unknown"));
        String tz = sdf.format(new Date());
        return tz.substring(0, 3) + sep + tz.substring(3);
    }

    private static int parseTimeField(String text, ParsePosition where, int maxlen, int remedy) {
        int cval;
        int index;
        if (text == null) {
            return remedy;
        }
        int retval = 0;
        int limit = index + Math.min(maxlen, text.length() - index);
        for (index = where.getIndex(); index < limit && (cval = text.charAt(index) - 48) >= 0 && cval <= 9; ++index) {
            retval = retval * 10 + cval;
        }
        if (index == where.getIndex()) {
            return remedy;
        }
        where.setIndex(index);
        return retval;
    }

    private static char skipOptionals(String text, ParsePosition where, String optionals) {
        char currch;
        char retval = ' ';
        while (where.getIndex() < text.length() && optionals.indexOf(currch = text.charAt(where.getIndex())) >= 0) {
            retval = currch != ' ' ? currch : retval;
            where.setIndex(where.getIndex() + 1);
        }
        return retval;
    }

    private static boolean skipString(String text, String victim, ParsePosition where) {
        if (text.startsWith(victim, where.getIndex())) {
            where.setIndex(where.getIndex() + victim.length());
            return true;
        }
        return false;
    }

    static GregorianCalendar newGreg() {
        GregorianCalendar retCal = new GregorianCalendar(Locale.ENGLISH);
        retCal.setTimeZone(new SimpleTimeZone(0, "UTC"));
        retCal.setLenient(false);
        retCal.set(14, 0);
        return retCal;
    }

    private static void adjustTimeZoneNicely(GregorianCalendar cal, TimeZone tz) {
        cal.setTimeZone(tz);
        int offset = (cal.get(15) + cal.get(16)) / 60000;
        cal.add(12, -offset);
    }

    static boolean parseTZoffset(String text, GregorianCalendar cal, ParsePosition initialWhere) {
        ParsePosition where = new ParsePosition(initialWhere.getIndex());
        TimeZone tz = new SimpleTimeZone(0, "GMT");
        char sign = DateConverter.skipOptionals(text, where, "Z+- ");
        boolean hadGMT = sign == 'Z' || DateConverter.skipString(text, "GMT", where) || DateConverter.skipString(text, "UTC", where);
        sign = !hadGMT ? sign : DateConverter.skipOptionals(text, where, "+- ");
        int tzHours = DateConverter.parseTimeField(text, where, 2, -999);
        DateConverter.skipOptionals(text, where, "': ");
        int tzMin = DateConverter.parseTimeField(text, where, 2, 0);
        DateConverter.skipOptionals(text, where, "' ");
        if (tzHours != -999) {
            int hrSign = sign == '-' ? -1 : 1;
            tz.setRawOffset(DateConverter.restrainTZoffset((long)hrSign * ((long)tzHours * 3600000L + (long)tzMin * 60000L)));
            DateConverter.updateZoneId(tz);
        } else if (!hadGMT) {
            String tzText = text.substring(initialWhere.getIndex()).trim();
            tz = TimeZone.getTimeZone(tzText);
            if ("GMT".equals(tz.getID())) {
                return false;
            }
            where.setIndex(text.length());
        }
        DateConverter.adjustTimeZoneNicely(cal, tz);
        initialWhere.setIndex(where.getIndex());
        return true;
    }

    private static void updateZoneId(TimeZone tz) {
        int offset = tz.getRawOffset();
        int pm = 43;
        if (offset < 0) {
            pm = 45;
            offset = -offset;
        }
        int hh = offset / 3600000;
        int mm = offset % 3600000 / 60000;
        if (offset == 0) {
            tz.setID("GMT");
        } else if (pm == 43 && hh <= 12) {
            tz.setID(String.format(Locale.US, "GMT+%02d:%02d", hh, mm));
        } else if (pm == 45 && hh <= 14) {
            tz.setID(String.format(Locale.US, "GMT-%02d:%02d", hh, mm));
        } else {
            tz.setID("unknown");
        }
    }

    private static GregorianCalendar parseBigEndianDate(String text, ParsePosition initialWhere) {
        ParsePosition where = new ParsePosition(initialWhere.getIndex());
        int year = DateConverter.parseTimeField(text, where, 4, 0);
        if (where.getIndex() != 4 + initialWhere.getIndex()) {
            return null;
        }
        DateConverter.skipOptionals(text, where, "/- ");
        int month = DateConverter.parseTimeField(text, where, 2, 1) - 1;
        DateConverter.skipOptionals(text, where, "/- ");
        int day = DateConverter.parseTimeField(text, where, 2, 1);
        DateConverter.skipOptionals(text, where, " T");
        int hour = DateConverter.parseTimeField(text, where, 2, 0);
        DateConverter.skipOptionals(text, where, ": ");
        int minute = DateConverter.parseTimeField(text, where, 2, 0);
        DateConverter.skipOptionals(text, where, ": ");
        int second = DateConverter.parseTimeField(text, where, 2, 0);
        char nextC = DateConverter.skipOptionals(text, where, ".");
        if (nextC == '.') {
            DateConverter.parseTimeField(text, where, 19, 0);
        }
        GregorianCalendar dest = DateConverter.newGreg();
        try {
            dest.set(year, month, day, hour, minute, second);
            dest.getTimeInMillis();
        }
        catch (IllegalArgumentException ill) {
            return null;
        }
        initialWhere.setIndex(where.getIndex());
        DateConverter.skipOptionals(text, initialWhere, " ");
        return dest;
    }

    private static GregorianCalendar parseSimpleDate(String text, String[] fmts, ParsePosition initialWhere) {
        for (String fmt : fmts) {
            ParsePosition where = new ParsePosition(initialWhere.getIndex());
            SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.ENGLISH);
            GregorianCalendar retCal = DateConverter.newGreg();
            sdf.setCalendar(retCal);
            if (sdf.parse(text, where) == null) continue;
            initialWhere.setIndex(where.getIndex());
            DateConverter.skipOptionals(text, initialWhere, " ");
            return retCal;
        }
        return null;
    }

    private static Calendar parseDate(String text, ParsePosition initialWhere) {
        int whereLen;
        if (text == null || text.isEmpty() || "D:".equals(text.trim())) {
            return null;
        }
        int longestLen = -999999;
        GregorianCalendar longestDate = null;
        ParsePosition where = new ParsePosition(initialWhere.getIndex());
        DateConverter.skipOptionals(text, where, " ");
        int startPosition = where.getIndex();
        GregorianCalendar retCal = DateConverter.parseBigEndianDate(text, where);
        if (retCal != null && (where.getIndex() == text.length() || DateConverter.parseTZoffset(text, retCal, where))) {
            whereLen = where.getIndex();
            if (whereLen == text.length()) {
                initialWhere.setIndex(whereLen);
                return retCal;
            }
            longestLen = whereLen;
            longestDate = retCal;
        }
        where.setIndex(startPosition);
        String[] formats = Character.isDigit(text.charAt(startPosition)) ? DIGIT_START_FORMATS : ALPHA_START_FORMATS;
        retCal = DateConverter.parseSimpleDate(text, formats, where);
        if (retCal != null && (where.getIndex() == text.length() || DateConverter.parseTZoffset(text, retCal, where))) {
            whereLen = where.getIndex();
            if (whereLen == text.length()) {
                initialWhere.setIndex(whereLen);
                return retCal;
            }
            if (whereLen > longestLen) {
                longestLen = whereLen;
                longestDate = retCal;
            }
        }
        if (longestDate != null) {
            initialWhere.setIndex(longestLen);
            return longestDate;
        }
        return retCal;
    }

    public static Calendar toCalendar(COSString text) {
        if (text == null) {
            return null;
        }
        return DateConverter.toCalendar(text.getString());
    }

    public static Calendar toCalendar(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        ParsePosition where = new ParsePosition(0);
        DateConverter.skipOptionals(text, where, " ");
        DateConverter.skipString(text, "D:", where);
        Calendar calendar = DateConverter.parseDate(text, where);
        if (calendar == null || where.getIndex() != text.length()) {
            return null;
        }
        return calendar;
    }
}

