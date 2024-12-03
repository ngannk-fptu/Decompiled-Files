/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.search.v2.lucene;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneUtils {
    private static final Logger log = LoggerFactory.getLogger(LuceneUtils.class);
    private static final AtomicInteger invalidDateWarningCount = new AtomicInteger();
    private static final int INVALID_DATE_MAX_WARNINGS = 20;
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final ThreadLocal<Calendar> TL_CAL = ThreadLocal.withInitial(() -> Calendar.getInstance(GMT, Locale.ROOT));
    private static final ThreadLocal<SimpleDateFormat[]> TL_FORMATS = ThreadLocal.withInitial(() -> {
        SimpleDateFormat[] arr = new SimpleDateFormat[Resolution.MILLISECOND.formatLen + 1];
        for (Resolution resolution : Resolution.values()) {
            arr[resolution.formatLen] = (SimpleDateFormat)resolution.format.clone();
        }
        return arr;
    });

    public static String dateToString(Date date) {
        return LuceneUtils.dateToString(date, Resolution.MILLISECOND);
    }

    public static String dateToString(Date date, Resolution resolution) {
        return LuceneUtils.timeToString(date.getTime(), resolution);
    }

    public static Date stringToDate(String s) {
        if (s != null && s.trim().length() > 0) {
            try {
                return LuceneUtils.dateStringToDate(s);
            }
            catch (ParseException e) {
                int currentErrorCount = invalidDateWarningCount.get();
                if (currentErrorCount <= 20) {
                    invalidDateWarningCount.incrementAndGet();
                    log.warn("Unable to parse a date found in the index because it uses an invalid encoding. Rebuilding the search index is recommended.");
                    if (currentErrorCount == 20) {
                        log.warn("Suppressing more warnings about invalid dates until the application is restarted.");
                    }
                }
                return new Date(Long.parseLong(s, 36));
            }
        }
        return new Date();
    }

    private static String timeToString(long time, Resolution resolution) {
        Date date = new Date(LuceneUtils.round(time, resolution));
        return TL_FORMATS.get()[resolution.formatLen].format(date);
    }

    private static Date dateStringToDate(String dateString) throws ParseException {
        try {
            return TL_FORMATS.get()[dateString.length()].parse(dateString);
        }
        catch (Exception e) {
            throw new ParseException("Input is not a valid date string: " + dateString, 0);
        }
    }

    private static long round(long time, Resolution resolution) {
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
                throw new IllegalArgumentException("unknown resolution " + resolution);
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

        public SimpleDateFormat getFormat() {
            return this.format;
        }

        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }
    }
}

