/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.util.StringUtil;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

final class DateHelper {
    static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
    static final String SQL_TIME_FORMAT = "HH:mm:ss";

    private DateHelper() {
    }

    static Date parseDate(String value) {
        try {
            return DateHelper.getUtilDateFormat().parse(value);
        }
        catch (ParseException e) {
            return (Date)DateHelper.throwRuntimeParseException(value, e, DATE_FORMAT);
        }
    }

    static Timestamp parseTimeStamp(String value) {
        try {
            return Timestamp.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            return (Timestamp)DateHelper.throwRuntimeParseException(value, new ParseException(e.getMessage(), 0), TIMESTAMP_FORMAT);
        }
    }

    static java.sql.Date parseSqlDate(String value) {
        try {
            return java.sql.Date.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            return (java.sql.Date)DateHelper.throwRuntimeParseException(value, new ParseException(value, 0), SQL_DATE_FORMAT);
        }
    }

    static Time parseSqlTime(String value) {
        try {
            return Time.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            return (Time)DateHelper.throwRuntimeParseException(value, new ParseException(value, 0), SQL_TIME_FORMAT);
        }
    }

    private static <T> T throwRuntimeParseException(String value, Exception e, String ... legalFormats) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < legalFormats.length; ++i) {
            sb.append("'").append(legalFormats[i]).append("'");
            if (i >= legalFormats.length - 2) continue;
            sb.append(", ");
        }
        throw new RuntimeException("Unable to parse date from value: '" + value + "' ! Valid format are: " + sb.toString() + ".", e);
    }

    private static DateFormat getTimestampFormat() {
        return new SimpleDateFormat(TIMESTAMP_FORMAT, StringUtil.LOCALE_INTERNAL);
    }

    private static DateFormat getSqlDateFormat() {
        return new SimpleDateFormat(SQL_DATE_FORMAT, StringUtil.LOCALE_INTERNAL);
    }

    private static DateFormat getUtilDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT, StringUtil.LOCALE_INTERNAL);
    }

    private static DateFormat getSqlTimeFormat() {
        return new SimpleDateFormat(SQL_TIME_FORMAT, StringUtil.LOCALE_INTERNAL);
    }
}

