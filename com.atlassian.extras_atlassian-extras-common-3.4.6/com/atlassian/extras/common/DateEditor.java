/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common;

import com.atlassian.extras.common.DateParsingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateEditor {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    static final String PERIOD_PREFIX = "P";
    private static final long MILLIS_IN_HOUR = 3600000L;
    public static final String UNLIMITED = "unlimited";
    private static final Pattern DURATION_PATTERN = Pattern.compile("Duration\\:([0-9]+)");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("P([0-9]+)H");
    private static final Pattern DATE_IN_MILLIS_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("^([1-2][0-9]{3}-(0[1-9]|1[0-2])-([0-2][0-9]|3[0-1]))(\\s|[T])?.*");
    static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Australia/Sydney");

    public static Date getDate(String dateString) {
        if (dateString == null || dateString.length() == 0) {
            throw new DateParsingException(dateString);
        }
        if (dateString.equals(UNLIMITED)) {
            return null;
        }
        Matcher durationMatcher = DURATION_PATTERN.matcher(dateString);
        if (durationMatcher.matches()) {
            long dateInMillis = System.currentTimeMillis() + Long.parseLong(durationMatcher.group(1));
            return new Date(dateInMillis);
        }
        Matcher periodMatcher = PERIOD_PATTERN.matcher(dateString);
        if (periodMatcher.matches()) {
            long dateInMillis = System.currentTimeMillis() + (long)Integer.parseInt(periodMatcher.group(1)) * 3600000L;
            return new Date(dateInMillis);
        }
        Matcher dateInMillisMatcher = DATE_IN_MILLIS_PATTERN.matcher(dateString);
        if (dateInMillisMatcher.matches()) {
            return new Date(Long.parseLong(dateString));
        }
        Matcher isoDateMatcher = ISO_DATE_PATTERN.matcher(dateString);
        if (isoDateMatcher.matches()) {
            try {
                return DateEditor.getDateFormat().parse(isoDateMatcher.group(1));
            }
            catch (ParseException e) {
                throw new DateParsingException(dateString, e);
            }
        }
        throw new DateParsingException(dateString);
    }

    public static String getString(Date date) {
        return date != null ? DateEditor.getDateFormat().format(date) : UNLIMITED;
    }

    private static DateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TIME_ZONE);
        return dateFormat;
    }
}

