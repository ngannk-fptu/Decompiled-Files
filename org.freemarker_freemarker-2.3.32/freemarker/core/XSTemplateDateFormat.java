/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ISOLikeTemplateDateFormat;
import freemarker.core.ISOLikeTemplateDateFormatFactory;
import freemarker.core.InvalidFormatParametersException;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import freemarker.template.utility.DateUtil;
import java.util.Date;
import java.util.TimeZone;

final class XSTemplateDateFormat
extends ISOLikeTemplateDateFormat {
    XSTemplateDateFormat(String settingValue, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        super(settingValue, parsingStart, dateType, zonelessInput, timeZone, factory, env);
    }

    @Override
    protected String format(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory calendarFactory) {
        return DateUtil.dateToXSString(date, datePart, timePart, offsetPart, accuracy, timeZone, calendarFactory);
    }

    @Override
    protected Date parseDate(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseXSDate(s, tz, calToDateConverter);
    }

    @Override
    protected Date parseTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseXSTime(s, tz, calToDateConverter);
    }

    @Override
    protected Date parseDateTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseXSDateTime(s, tz, calToDateConverter);
    }

    @Override
    protected String getDateDescription() {
        return "W3C XML Schema date";
    }

    @Override
    protected String getTimeDescription() {
        return "W3C XML Schema time";
    }

    @Override
    protected String getDateTimeDescription() {
        return "W3C XML Schema dateTime";
    }

    @Override
    protected boolean isXSMode() {
        return true;
    }
}

