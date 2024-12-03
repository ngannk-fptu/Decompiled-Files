/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.FormatConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="date")
public class DateTool
extends FormatConfig {
    @Deprecated
    public static final String DEFAULT_FORMAT_KEY = "format";
    @Deprecated
    public static final String DEFAULT_LOCALE_KEY = "locale";
    public static final String TIMEZONE_KEY = "timezone";
    private TimeZone timezone = TimeZone.getDefault();

    @Override
    protected void configure(ValueParser values) {
        super.configure(values);
        String tzId = values.getString(TIMEZONE_KEY);
        if (tzId != null) {
            this.setTimeZone(TimeZone.getTimeZone(tzId));
        }
    }

    protected void setTimeZone(TimeZone timezone) {
        if (timezone == null) {
            throw new NullPointerException("timezone may not be null");
        }
        this.timezone = timezone;
    }

    public static final long getSystemTime() {
        return DateTool.getSystemCalendar().getTime().getTime();
    }

    public static final Date getSystemDate() {
        return DateTool.getSystemCalendar().getTime();
    }

    public static final Calendar getSystemCalendar() {
        return Calendar.getInstance();
    }

    public TimeZone getTimeZone() {
        return this.timezone;
    }

    public Date getDate() {
        return this.getCalendar().getTime();
    }

    public Calendar getCalendar() {
        return Calendar.getInstance(this.getTimeZone(), this.getLocale());
    }

    public Integer getYear() {
        return this.getYear(this.getCalendar());
    }

    public Integer getYear(Object date) {
        return this.getValue(1, date);
    }

    public Integer getMonth() {
        return this.getMonth(this.getCalendar());
    }

    public Integer getMonth(Object date) {
        return this.getValue(2, date);
    }

    public Integer getDay() {
        return this.getDay(this.getCalendar());
    }

    public Integer getDay(Object date) {
        return this.getValue(5, date);
    }

    public Integer getValue(Object field) {
        return this.getValue(field, (Object)this.getCalendar());
    }

    public Integer getValue(Object field, Object date) {
        int fieldValue;
        if (field == null) {
            return null;
        }
        if (field instanceof Integer) {
            int n = (Integer)field;
        }
        String fstr = field.toString().toUpperCase();
        try {
            Field clsf = Calendar.class.getField(fstr);
            fieldValue = clsf.getInt(Calendar.getInstance());
        }
        catch (Exception e) {
            return null;
        }
        return this.getValue(fieldValue, date);
    }

    public Integer getValue(int field, Object date) {
        Calendar cal = this.toCalendar(date);
        if (cal == null) {
            return null;
        }
        return cal.get(field);
    }

    public String get(String format) {
        return this.format(format, this.getDate());
    }

    public String get(String dateStyle, String timeStyle) {
        return this.format(dateStyle, timeStyle, this.getDate(), this.getLocale());
    }

    public String format(Object obj) {
        return this.format(this.getFormat(), obj);
    }

    public String format(String format, Object obj) {
        return this.format(format, obj, this.getLocale());
    }

    public String format(String format, Object obj, Locale locale) {
        return this.format(format, obj, locale, this.getTimeZone());
    }

    public String format(String format, Object obj, Locale locale, TimeZone timezone) {
        Date date = this.toDate(obj);
        DateFormat df = this.getDateFormat(format, locale, timezone);
        if (date == null || df == null) {
            return null;
        }
        return df.format(date);
    }

    public String format(String dateStyle, String timeStyle, Object obj) {
        return this.format(dateStyle, timeStyle, obj, this.getLocale());
    }

    public String format(String dateStyle, String timeStyle, Object obj, Locale locale) {
        return this.format(dateStyle, timeStyle, obj, locale, this.getTimeZone());
    }

    public String format(String dateStyle, String timeStyle, Object obj, Locale locale, TimeZone timezone) {
        Date date = this.toDate(obj);
        DateFormat df = this.getDateFormat(dateStyle, timeStyle, locale, timezone);
        if (date == null || df == null) {
            return null;
        }
        return df.format(date);
    }

    public DateFormat getDateFormat(String format, Locale locale, TimeZone timezone) {
        return ConversionUtils.getDateFormat(format, locale, timezone);
    }

    public DateFormat getDateFormat(String dateStyle, String timeStyle, Locale locale, TimeZone timezone) {
        return ConversionUtils.getDateFormat(dateStyle, timeStyle, locale, timezone);
    }

    @Deprecated
    protected DateFormat getDateFormat(int dateStyle, int timeStyle, Locale locale, TimeZone timezone) {
        return ConversionUtils.getDateFormat(dateStyle, timeStyle, locale, timezone);
    }

    @Deprecated
    protected int getStyleAsInt(String style) {
        return ConversionUtils.getDateStyleAsInt(style);
    }

    public Date toDate(Object obj) {
        return this.toDate(this.getFormat(), obj, this.getLocale(), this.getTimeZone());
    }

    public Date toDate(String format, Object obj) {
        return this.toDate(format, obj, this.getLocale(), this.getTimeZone());
    }

    public Date toDate(String format, Object obj, Locale locale) {
        return this.toDate(format, obj, locale, this.getTimeZone());
    }

    public Date toDate(String format, Object obj, Locale locale, TimeZone timezone) {
        return ConversionUtils.toDate(obj, format, locale, timezone);
    }

    public Calendar toCalendar(Object obj) {
        return this.toCalendar(obj, this.getLocale());
    }

    public Calendar toCalendar(Object obj, Locale locale) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Calendar) {
            return (Calendar)obj;
        }
        Date date = this.toDate(obj);
        if (date == null) {
            return null;
        }
        if (locale == null) {
            locale = this.getLocale();
        }
        return ConversionUtils.toCalendar(date, locale);
    }

    public String toLocalizedPattern(String format, Locale locale) {
        DateFormat df = this.getDateFormat(format, locale, this.getTimeZone());
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat)df).toLocalizedPattern();
        }
        return null;
    }

    public String toString() {
        return this.format(this.getFormat(), this.getDate());
    }
}

