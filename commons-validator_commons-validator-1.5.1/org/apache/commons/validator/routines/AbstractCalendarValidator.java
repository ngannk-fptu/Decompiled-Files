/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.validator.routines.AbstractFormatValidator;

public abstract class AbstractCalendarValidator
extends AbstractFormatValidator {
    private static final long serialVersionUID = -1410008585975827379L;
    private final int dateStyle;
    private final int timeStyle;

    public AbstractCalendarValidator(boolean strict, int dateStyle, int timeStyle) {
        super(strict);
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }

    @Override
    public boolean isValid(String value, String pattern, Locale locale) {
        Object parsedValue = this.parse(value, pattern, locale, null);
        return parsedValue != null;
    }

    public String format(Object value, TimeZone timeZone) {
        return this.format(value, null, null, timeZone);
    }

    public String format(Object value, String pattern, TimeZone timeZone) {
        return this.format(value, pattern, null, timeZone);
    }

    public String format(Object value, Locale locale, TimeZone timeZone) {
        return this.format(value, null, locale, timeZone);
    }

    @Override
    public String format(Object value, String pattern, Locale locale) {
        return this.format(value, pattern, locale, null);
    }

    public String format(Object value, String pattern, Locale locale, TimeZone timeZone) {
        DateFormat formatter = (DateFormat)this.getFormat(pattern, locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        } else if (value instanceof Calendar) {
            formatter.setTimeZone(((Calendar)value).getTimeZone());
        }
        return this.format(value, formatter);
    }

    @Override
    protected String format(Object value, Format formatter) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            value = ((Calendar)value).getTime();
        }
        return formatter.format(value);
    }

    protected Object parse(String value, String pattern, Locale locale, TimeZone timeZone) {
        String string = value = value == null ? null : value.trim();
        if (value == null || value.length() == 0) {
            return null;
        }
        DateFormat formatter = (DateFormat)this.getFormat(pattern, locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        return this.parse(value, formatter);
    }

    @Override
    protected abstract Object processParsedValue(Object var1, Format var2);

    @Override
    protected Format getFormat(String pattern, Locale locale) {
        boolean usePattern;
        DateFormat formatter = null;
        boolean bl = usePattern = pattern != null && pattern.length() > 0;
        if (!usePattern) {
            formatter = (DateFormat)this.getFormat(locale);
        } else if (locale == null) {
            formatter = new SimpleDateFormat(pattern);
        } else {
            DateFormatSymbols symbols = new DateFormatSymbols(locale);
            formatter = new SimpleDateFormat(pattern, symbols);
        }
        formatter.setLenient(false);
        return formatter;
    }

    protected Format getFormat(Locale locale) {
        DateFormat formatter = null;
        if (this.dateStyle >= 0 && this.timeStyle >= 0) {
            formatter = locale == null ? DateFormat.getDateTimeInstance(this.dateStyle, this.timeStyle) : DateFormat.getDateTimeInstance(this.dateStyle, this.timeStyle, locale);
        } else if (this.timeStyle >= 0) {
            formatter = locale == null ? DateFormat.getTimeInstance(this.timeStyle) : DateFormat.getTimeInstance(this.timeStyle, locale);
        } else {
            int useDateStyle = this.dateStyle >= 0 ? this.dateStyle : 3;
            formatter = locale == null ? DateFormat.getDateInstance(useDateStyle) : DateFormat.getDateInstance(useDateStyle, locale);
        }
        formatter.setLenient(false);
        return formatter;
    }

    protected int compare(Calendar value, Calendar compare, int field) {
        int result = 0;
        result = this.calculateCompareResult(value, compare, 1);
        if (result != 0 || field == 1) {
            return result;
        }
        if (field == 3) {
            return this.calculateCompareResult(value, compare, 3);
        }
        if (field == 6) {
            return this.calculateCompareResult(value, compare, 6);
        }
        result = this.calculateCompareResult(value, compare, 2);
        if (result != 0 || field == 2) {
            return result;
        }
        if (field == 4) {
            return this.calculateCompareResult(value, compare, 4);
        }
        result = this.calculateCompareResult(value, compare, 5);
        if (result != 0 || field == 5 || field == 7 || field == 8) {
            return result;
        }
        return this.compareTime(value, compare, field);
    }

    protected int compareTime(Calendar value, Calendar compare, int field) {
        int result = 0;
        result = this.calculateCompareResult(value, compare, 11);
        if (result != 0 || field == 10 || field == 11) {
            return result;
        }
        result = this.calculateCompareResult(value, compare, 12);
        if (result != 0 || field == 12) {
            return result;
        }
        result = this.calculateCompareResult(value, compare, 13);
        if (result != 0 || field == 13) {
            return result;
        }
        if (field == 14) {
            return this.calculateCompareResult(value, compare, 14);
        }
        throw new IllegalArgumentException("Invalid field: " + field);
    }

    protected int compareQuarters(Calendar value, Calendar compare, int monthOfFirstQuarter) {
        int compareQuarter;
        int valueQuarter = this.calculateQuarter(value, monthOfFirstQuarter);
        if (valueQuarter < (compareQuarter = this.calculateQuarter(compare, monthOfFirstQuarter))) {
            return -1;
        }
        if (valueQuarter > compareQuarter) {
            return 1;
        }
        return 0;
    }

    private int calculateQuarter(Calendar calendar, int monthOfFirstQuarter) {
        int year = calendar.get(1);
        int month = calendar.get(2) + 1;
        int relativeMonth = month >= monthOfFirstQuarter ? month - monthOfFirstQuarter : month + (12 - monthOfFirstQuarter);
        int quarter = relativeMonth / 3 + 1;
        if (month < monthOfFirstQuarter) {
            --year;
        }
        return year * 10 + quarter;
    }

    private int calculateCompareResult(Calendar value, Calendar compare, int field) {
        int difference = value.get(field) - compare.get(field);
        if (difference < 0) {
            return -1;
        }
        if (difference > 0) {
            return 1;
        }
        return 0;
    }
}

