/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.validator.routines.AbstractCalendarValidator;

public class DateValidator
extends AbstractCalendarValidator {
    private static final long serialVersionUID = -3966328400469953190L;
    private static final DateValidator VALIDATOR = new DateValidator();

    public static DateValidator getInstance() {
        return VALIDATOR;
    }

    public DateValidator() {
        this(true, 3);
    }

    public DateValidator(boolean strict, int dateStyle) {
        super(strict, dateStyle, -1);
    }

    public Date validate(String value) {
        return (Date)this.parse(value, null, null, null);
    }

    public Date validate(String value, TimeZone timeZone) {
        return (Date)this.parse(value, null, null, timeZone);
    }

    public Date validate(String value, String pattern) {
        return (Date)this.parse(value, pattern, null, null);
    }

    public Date validate(String value, String pattern, TimeZone timeZone) {
        return (Date)this.parse(value, pattern, null, timeZone);
    }

    public Date validate(String value, Locale locale) {
        return (Date)this.parse(value, null, locale, null);
    }

    public Date validate(String value, Locale locale, TimeZone timeZone) {
        return (Date)this.parse(value, null, locale, timeZone);
    }

    public Date validate(String value, String pattern, Locale locale) {
        return (Date)this.parse(value, pattern, locale, null);
    }

    public Date validate(String value, String pattern, Locale locale, TimeZone timeZone) {
        return (Date)this.parse(value, pattern, locale, timeZone);
    }

    public int compareDates(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue = this.getCalendar(value, timeZone);
        Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 5);
    }

    public int compareWeeks(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue = this.getCalendar(value, timeZone);
        Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 3);
    }

    public int compareMonths(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue = this.getCalendar(value, timeZone);
        Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 2);
    }

    public int compareQuarters(Date value, Date compare, TimeZone timeZone) {
        return this.compareQuarters(value, compare, timeZone, 1);
    }

    public int compareQuarters(Date value, Date compare, TimeZone timeZone, int monthOfFirstQuarter) {
        Calendar calendarValue = this.getCalendar(value, timeZone);
        Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return super.compareQuarters(calendarValue, calendarCompare, monthOfFirstQuarter);
    }

    public int compareYears(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue = this.getCalendar(value, timeZone);
        Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 1);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        return value;
    }

    private Calendar getCalendar(Date value, TimeZone timeZone) {
        Calendar calendar = null;
        calendar = timeZone != null ? Calendar.getInstance(timeZone) : Calendar.getInstance();
        calendar.setTime(value);
        return calendar;
    }
}

