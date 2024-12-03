/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.model;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CalendarDateFormatFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarDateFormatFactory.class);
    private static final String DATETIME_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final String DATETIME_UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final String TIME_PATTERN = "HHmmss";
    private static final String TIME_UTC_PATTERN = "HHmmss'Z'";

    private CalendarDateFormatFactory() {
    }

    public static java.text.DateFormat getInstance(String pattern) {
        java.text.DateFormat instance;
        if (pattern.equals(DATETIME_PATTERN) || pattern.equals(DATETIME_UTC_PATTERN)) {
            instance = new DateTimeFormat(pattern);
        } else if (pattern.equals(DATE_PATTERN)) {
            instance = new DateFormat(pattern);
        } else if (pattern.equals(TIME_PATTERN) || pattern.equals(TIME_UTC_PATTERN)) {
            instance = new TimeFormat(pattern);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("unexpected date format pattern: " + pattern);
            }
            instance = new SimpleDateFormat(pattern);
        }
        return instance;
    }

    private static Calendar makeCalendar(boolean lenient, TimeZone timeZone, int year, int zeroBasedMonth, int day, int hour, int minutes, int seconds) {
        GregorianCalendar cal = new GregorianCalendar(timeZone);
        cal.setLenient(lenient);
        cal.set(year, zeroBasedMonth, day, hour, minutes, seconds);
        cal.set(14, 0);
        return cal;
    }

    private static Calendar makeCalendar(boolean lenient, TimeZone timeZone, int year, int month, int day) {
        return CalendarDateFormatFactory.makeCalendar(lenient, timeZone, year, month, day, 0, 0, 0);
    }

    private static void appendPadded(StringBuffer toAppendTo, int value, int fieldWidth) {
        String s = Integer.toString(value);
        int max = fieldWidth - s.length();
        for (int i = 0; i < max; ++i) {
            toAppendTo.append("0");
        }
        toAppendTo.append(s);
    }

    private static class TimeFormat
    extends CalendarDateFormat {
        private static final long serialVersionUID = -1367114409994225425L;
        final boolean patternEndsWithZ;

        public TimeFormat(String pattern) {
            super(pattern);
            this.patternEndsWithZ = pattern.endsWith("'Z'");
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            GregorianCalendar cal = new GregorianCalendar(this.getTimeZone());
            cal.setTimeInMillis(date.getTime());
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(11), 2);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(12), 2);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(13), 2);
            if (this.patternEndsWithZ) {
                toAppendTo.append("Z");
            }
            return toAppendTo;
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            if (this.patternEndsWithZ) {
                if (source.length() > CalendarDateFormatFactory.TIME_UTC_PATTERN.length() && !this.isLenient()) {
                    pos.setErrorIndex(CalendarDateFormatFactory.TIME_UTC_PATTERN.length());
                    return null;
                }
            } else if (source.length() > CalendarDateFormatFactory.TIME_PATTERN.length() && !this.isLenient()) {
                pos.setErrorIndex(CalendarDateFormatFactory.TIME_PATTERN.length());
                return null;
            }
            try {
                if (this.patternEndsWithZ && source.charAt(6) != 'Z') {
                    pos.setErrorIndex(6);
                    return null;
                }
                int hour = Integer.parseInt(source.substring(0, 2));
                int minute = Integer.parseInt(source.substring(2, 4));
                int second = Integer.parseInt(source.substring(4, 6));
                Date d = CalendarDateFormatFactory.makeCalendar(this.isLenient(), this.getTimeZone(), 1970, 0, 1, hour, minute, second).getTime();
                pos.setIndex(6);
                return d;
            }
            catch (RuntimeException e) {
                return null;
            }
        }
    }

    private static class DateFormat
    extends CalendarDateFormat {
        private static final long serialVersionUID = -7626077667268431779L;

        public DateFormat(String pattern) {
            super(pattern);
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            Calendar cal = Calendar.getInstance(this.getTimeZone());
            cal.setTimeInMillis(date.getTime());
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(1), 4);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(2) + 1, 2);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(5), 2);
            return toAppendTo;
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            if (source.length() > CalendarDateFormatFactory.DATE_PATTERN.length() && !this.isLenient()) {
                pos.setErrorIndex(CalendarDateFormatFactory.DATE_PATTERN.length());
                return null;
            }
            try {
                int year = Integer.parseInt(source.substring(0, 4));
                int month = Integer.parseInt(source.substring(4, 6)) - 1;
                int day = Integer.parseInt(source.substring(6, 8));
                Date d = CalendarDateFormatFactory.makeCalendar(this.isLenient(), this.getTimeZone(), year, month, day).getTime();
                pos.setIndex(8);
                return d;
            }
            catch (RuntimeException e) {
                return null;
            }
        }
    }

    private static class DateTimeFormat
    extends CalendarDateFormat {
        private static final long serialVersionUID = 3005824302269636122L;
        final boolean patternEndsWithZ;

        public DateTimeFormat(String pattern) {
            super(pattern);
            this.patternEndsWithZ = pattern.endsWith("'Z'");
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            GregorianCalendar cal = new GregorianCalendar(this.getTimeZone());
            cal.setTimeInMillis(date.getTime());
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(1), 4);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(2) + 1, 2);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(5), 2);
            toAppendTo.append("T");
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(11), 2);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(12), 2);
            CalendarDateFormatFactory.appendPadded(toAppendTo, cal.get(13), 2);
            if (this.patternEndsWithZ) {
                toAppendTo.append("Z");
            }
            return toAppendTo;
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            if (this.patternEndsWithZ) {
                if (source.length() > CalendarDateFormatFactory.DATETIME_UTC_PATTERN.length() && !this.isLenient()) {
                    pos.setErrorIndex(CalendarDateFormatFactory.DATETIME_UTC_PATTERN.length());
                    return null;
                }
            } else if (source.length() > CalendarDateFormatFactory.DATETIME_PATTERN.length() && !this.isLenient()) {
                pos.setErrorIndex(CalendarDateFormatFactory.DATETIME_PATTERN.length());
                return null;
            }
            try {
                if (source.charAt(8) != 'T') {
                    pos.setErrorIndex(8);
                    return null;
                }
                if (this.patternEndsWithZ && source.charAt(15) != 'Z') {
                    pos.setErrorIndex(15);
                    return null;
                }
                int year = Integer.parseInt(source.substring(0, 4));
                int month = Integer.parseInt(source.substring(4, 6)) - 1;
                int day = Integer.parseInt(source.substring(6, 8));
                int hour = Integer.parseInt(source.substring(9, 11));
                int minute = Integer.parseInt(source.substring(11, 13));
                int second = Integer.parseInt(source.substring(13, 15));
                Date d = CalendarDateFormatFactory.makeCalendar(this.isLenient(), this.getTimeZone(), year, month, day, hour, minute, second).getTime();
                pos.setIndex(15);
                return d;
            }
            catch (RuntimeException e) {
                return null;
            }
        }
    }

    private static abstract class CalendarDateFormat
    extends java.text.DateFormat {
        private static final long serialVersionUID = -4191402739860280205L;
        private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();
        private final String pattern;
        private boolean lenient = true;
        private TimeZone timeZone = DEFAULT_TIME_ZONE;

        public CalendarDateFormat(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public TimeZone getTimeZone() {
            return this.timeZone;
        }

        @Override
        public void setTimeZone(TimeZone tz) {
            this.timeZone = tz;
        }

        @Override
        public void setLenient(boolean lenient) {
            this.lenient = lenient;
        }

        @Override
        public boolean isLenient() {
            return this.lenient;
        }

        @Override
        public Calendar getCalendar() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCalendar(Calendar c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NumberFormat getNumberFormat() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNumberFormat(NumberFormat n) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object clone() {
            CalendarDateFormat f = (CalendarDateFormat)CalendarDateFormatFactory.getInstance(this.pattern);
            f.setTimeZone(this.getTimeZone());
            f.setLenient(this.isLenient());
            return f;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            CalendarDateFormat that = (CalendarDateFormat)o;
            return this.lenient == that.lenient && this.pattern.equals(that.pattern) && this.timeZone.equals(that.timeZone);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.pattern.hashCode();
            result = 31 * result + (this.lenient ? 1 : 0);
            result = 31 * result + this.timeZone.hashCode();
            return result;
        }
    }
}

