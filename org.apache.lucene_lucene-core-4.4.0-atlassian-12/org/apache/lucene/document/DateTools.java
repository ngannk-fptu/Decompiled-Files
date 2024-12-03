/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTools {
    static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final ThreadLocal<Calendar> TL_CAL = new ThreadLocal<Calendar>(){

        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance(GMT, Locale.ROOT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat[]> TL_FORMATS = new ThreadLocal<SimpleDateFormat[]>(){

        @Override
        protected SimpleDateFormat[] initialValue() {
            SimpleDateFormat[] arr = new SimpleDateFormat[Resolution.MILLISECOND.formatLen + 1];
            for (Resolution resolution : Resolution.values()) {
                arr[resolution.formatLen] = (SimpleDateFormat)resolution.format.clone();
            }
            return arr;
        }
    };

    private DateTools() {
    }

    public static String dateToString(Date date, Resolution resolution) {
        return DateTools.timeToString(date.getTime(), resolution);
    }

    public static String timeToString(long time, Resolution resolution) {
        Date date = new Date(DateTools.round(time, resolution));
        return TL_FORMATS.get()[resolution.formatLen].format(date);
    }

    public static long stringToTime(String dateString) throws ParseException {
        return DateTools.stringToDate(dateString).getTime();
    }

    public static Date stringToDate(String dateString) throws ParseException {
        try {
            return TL_FORMATS.get()[dateString.length()].parse(dateString);
        }
        catch (Exception e) {
            throw new ParseException("Input is not a valid date string: " + dateString, 0);
        }
    }

    public static Date round(Date date, Resolution resolution) {
        return new Date(DateTools.round(date.getTime(), resolution));
    }

    public static long round(long time, Resolution resolution) {
        Calendar calInstance = TL_CAL.get();
        calInstance.setTimeInMillis(time);
        switch (resolution) {
            case YEAR: {
                calInstance.set(2, 0);
            }
            case MONTH: {
                calInstance.set(5, 1);
            }
            case DAY: {
                calInstance.set(11, 0);
            }
            case HOUR: {
                calInstance.set(12, 0);
            }
            case MINUTE: {
                calInstance.set(13, 0);
            }
            case SECOND: {
                calInstance.set(14, 0);
            }
            case MILLISECOND: {
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown resolution " + (Object)((Object)resolution));
            }
        }
        return calInstance.getTimeInMillis();
    }

    public static enum Resolution {
        YEAR(4),
        MONTH(6),
        DAY(8),
        HOUR(10),
        MINUTE(12),
        SECOND(14),
        MILLISECOND(17);

        final int formatLen;
        final SimpleDateFormat format;

        private Resolution(int formatLen) {
            this.formatLen = formatLen;
            this.format = new SimpleDateFormat("yyyyMMddHHmmssSSS".substring(0, formatLen), Locale.ROOT);
            this.format.setTimeZone(GMT);
        }

        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }
    }
}

