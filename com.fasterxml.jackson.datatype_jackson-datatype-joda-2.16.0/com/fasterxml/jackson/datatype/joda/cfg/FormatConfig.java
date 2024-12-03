/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeFieldType
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 *  org.joda.time.format.ISOPeriodFormat
 */
package com.fasterxml.jackson.datatype.joda.cfg;

import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;
import java.util.Arrays;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;

public class FormatConfig {
    private static final DateTimeZone DEFAULT_TZ = DateTimeZone.getDefault();
    public static final JacksonJodaDateFormat DEFAULT_DATEONLY_FORMAT = FormatConfig.createUTC(ISODateTimeFormat.date());
    public static final JacksonJodaDateFormat DEFAULT_TIMEONLY_FORMAT = FormatConfig.createUTC(ISODateTimeFormat.time());
    public static final JacksonJodaDateFormat DEFAULT_DATETIME_PARSER = FormatConfig.createUTC(ISODateTimeFormat.dateTimeParser());
    public static final JacksonJodaDateFormat DEFAULT_DATETIME_PRINTER;
    @Deprecated
    public static final JacksonJodaDateFormat DEFAULT_DATETIME_FORMAT;
    public static final JacksonJodaDateFormat DEFAULT_LOCAL_DATEONLY_FORMAT;
    public static final JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PRINTER;
    public static final JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PARSER;
    public static final JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PRINTER;
    public static final JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PARSER;
    public static final JacksonJodaPeriodFormat DEFAULT_PERIOD_FORMAT;
    public static final JacksonJodaDateFormat DEFAULT_YEAR_MONTH_FORMAT;
    public static final JacksonJodaDateFormat DEFAULT_MONTH_DAY_FORMAT;

    private static final JacksonJodaDateFormat createUTC(DateTimeFormatter f) {
        return new JacksonJodaDateFormat(f.withZoneUTC());
    }

    private static final JacksonJodaDateFormat createDefaultTZ(DateTimeFormatter f) {
        return new JacksonJodaDateFormat(f.withZone(DEFAULT_TZ));
    }

    private static final JacksonJodaDateFormat createMonthDayFormat() {
        return new JacksonJodaDateFormat(ISODateTimeFormat.forFields(Arrays.asList(DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()), (boolean)true, (boolean)true));
    }

    static {
        DEFAULT_DATETIME_FORMAT = DEFAULT_DATETIME_PRINTER = FormatConfig.createUTC(ISODateTimeFormat.dateTime());
        DEFAULT_LOCAL_DATEONLY_FORMAT = FormatConfig.createDefaultTZ(ISODateTimeFormat.date());
        DEFAULT_LOCAL_TIMEONLY_PRINTER = FormatConfig.createDefaultTZ(ISODateTimeFormat.time());
        DEFAULT_LOCAL_TIMEONLY_PARSER = FormatConfig.createDefaultTZ(ISODateTimeFormat.localTimeParser());
        DEFAULT_LOCAL_DATETIME_PRINTER = FormatConfig.createDefaultTZ(ISODateTimeFormat.dateTime());
        DEFAULT_LOCAL_DATETIME_PARSER = FormatConfig.createDefaultTZ(ISODateTimeFormat.localDateOptionalTimeParser());
        DEFAULT_PERIOD_FORMAT = new JacksonJodaPeriodFormat(ISOPeriodFormat.standard());
        DEFAULT_YEAR_MONTH_FORMAT = new JacksonJodaDateFormat(ISODateTimeFormat.yearMonth());
        DEFAULT_MONTH_DAY_FORMAT = FormatConfig.createMonthDayFormat();
    }
}

