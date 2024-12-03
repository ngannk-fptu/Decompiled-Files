/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.SkipSetters;
import org.apache.velocity.tools.generic.LocaleConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="convert")
@SkipSetters
public class ConversionTool
extends LocaleConfig {
    public static final String STRINGS_DELIMITER_FORMAT_KEY = "stringsDelimiter";
    public static final String STRINGS_TRIM_KEY = "trimStrings";
    public static final String DATE_FORMAT_KEY = "dateFormat";
    public static final String NUMBER_FORMAT_KEY = "numberFormat";
    public static final String DEFAULT_STRINGS_DELIMITER = ",";
    public static final boolean DEFAULT_STRINGS_TRIM = true;
    public static final String DEFAULT_NUMBER_FORMAT = "default";
    public static final String DEFAULT_DATE_FORMAT = "default";
    private String stringsDelimiter = ",";
    private boolean stringsTrim = true;
    private String numberFormat = "default";
    private String dateFormat = "default";

    @Override
    protected void configure(ValueParser values) {
        String numberFormat;
        String dateFormat;
        super.configure(values);
        String delimiter = values.getString(STRINGS_DELIMITER_FORMAT_KEY);
        if (delimiter != null) {
            this.setStringsDelimiter(delimiter);
        }
        if ((dateFormat = values.getString(DATE_FORMAT_KEY)) != null) {
            this.setDateFormat(dateFormat);
        }
        if ((numberFormat = values.getString(NUMBER_FORMAT_KEY)) != null) {
            this.setNumberFormat(numberFormat);
        }
    }

    protected final void setStringsDelimiter(String stringsDelimiter) {
        this.stringsDelimiter = stringsDelimiter;
    }

    public final String getStringsDelimiter() {
        return this.stringsDelimiter;
    }

    protected final void setStringsTrim(boolean stringsTrim) {
        this.stringsTrim = stringsTrim;
    }

    public final boolean getStringsTrim() {
        return this.stringsTrim;
    }

    protected final void setNumberFormat(String format) {
        this.numberFormat = format;
    }

    public final String getNumberFormat() {
        return this.numberFormat;
    }

    protected final void setDateFormat(String format) {
        this.dateFormat = format;
    }

    public final String getDateFormat() {
        return this.dateFormat;
    }

    public String toString(Object value) {
        return ConversionUtils.toString(value);
    }

    public Boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        String s = this.toString(value);
        return s != null ? this.parseBoolean(s) : null;
    }

    public Integer toInteger(Object value) {
        if (value == null || value instanceof Integer) {
            return (Integer)value;
        }
        Number num = this.toNumber(value);
        return num.intValue();
    }

    public Double toDouble(Object value) {
        if (value == null || value instanceof Double) {
            return (Double)value;
        }
        Number num = this.toNumber(value);
        return new Double(num.doubleValue());
    }

    public Number toNumber(Object value) {
        Number number = ConversionUtils.toNumber(value, false);
        if (number != null) {
            return number;
        }
        String s = this.toString(value);
        if (s == null || s.length() == 0) {
            return null;
        }
        return this.parseNumber(s);
    }

    public Locale toLocale(Object value) {
        if (value instanceof Locale) {
            return (Locale)value;
        }
        String s = this.toString(value);
        if (s == null || s.length() == 0) {
            return null;
        }
        return this.parseLocale(s);
    }

    public Date toDate(Object value) {
        Date d = ConversionUtils.toDate(value);
        if (d != null) {
            return d;
        }
        String s = this.toString(value);
        if (s == null || s.length() == 0) {
            return null;
        }
        return this.parseDate(s);
    }

    public Calendar toCalendar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return (Calendar)value;
        }
        Date date = this.toDate(value);
        if (date == null) {
            return null;
        }
        return ConversionUtils.toCalendar(date, this.getLocale());
    }

    public String[] toStrings(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String[]) {
            return (String[])value;
        }
        String[] strings = null;
        if (value instanceof Collection) {
            Collection values = (Collection)value;
            if (!values.isEmpty()) {
                strings = new String[values.size()];
                int index = 0;
                Iterator i = values.iterator();
                while (i.hasNext()) {
                    strings[index++] = this.toString(i.next());
                }
            }
        } else if (value.getClass().isArray()) {
            strings = new String[Array.getLength(value)];
            for (int i = 0; i < strings.length; ++i) {
                strings[i] = this.toString(Array.get(value, i));
            }
        } else {
            strings = this.parseStringList(this.toString(value));
        }
        return strings;
    }

    public Boolean[] toBooleans(Object value) {
        if (value != null && !value.getClass().isArray()) {
            value = this.toStrings(value);
        }
        if (value == null) {
            return null;
        }
        Boolean[] bools = new Boolean[Array.getLength(value)];
        for (int i = 0; i < bools.length; ++i) {
            bools[i] = this.toBoolean(Array.get(value, i));
        }
        return bools;
    }

    public Boolean[] toBooleans(Collection values) {
        if (values == null || !values.isEmpty()) {
            return null;
        }
        Boolean[] bools = new Boolean[values.size()];
        int index = 0;
        for (Object val : values) {
            bools[index++] = this.toBoolean(val);
        }
        return bools;
    }

    public Number[] toNumbers(Object value) {
        if (value != null && !value.getClass().isArray()) {
            value = this.toStrings(value);
        }
        if (value == null) {
            return null;
        }
        Number[] numbers = new Number[Array.getLength(value)];
        for (int i = 0; i < numbers.length; ++i) {
            numbers[i] = this.toNumber(Array.get(value, i));
        }
        return numbers;
    }

    public Number[] toNumbers(Collection values) {
        if (values == null || !values.isEmpty()) {
            return null;
        }
        Number[] numbers = new Number[values.size()];
        int index = 0;
        for (Object val : values) {
            numbers[index++] = this.toNumber(val);
        }
        return numbers;
    }

    public int[] toInts(Object value) {
        Number[] numbers = this.toNumbers(value);
        if (numbers == null) {
            return null;
        }
        int[] ints = new int[numbers.length];
        for (int i = 0; i < ints.length; ++i) {
            if (numbers[i] == null) continue;
            ints[i] = numbers[i].intValue();
        }
        return ints;
    }

    public int[] toIntegers(Object value) {
        return this.toInts(value);
    }

    public double[] toDoubles(Object value) {
        Number[] numbers = this.toNumbers(value);
        if (numbers == null) {
            return null;
        }
        double[] doubles = new double[numbers.length];
        for (int i = 0; i < doubles.length; ++i) {
            if (numbers[i] == null) continue;
            doubles[i] = numbers[i].doubleValue();
        }
        return doubles;
    }

    public Locale[] toLocales(Object value) {
        if (value != null && !value.getClass().isArray()) {
            value = this.toStrings(value);
        }
        if (value == null) {
            return null;
        }
        Locale[] locales = new Locale[Array.getLength(value)];
        for (int i = 0; i < locales.length; ++i) {
            locales[i] = this.toLocale(Array.get(value, i));
        }
        return locales;
    }

    public Locale[] toLocales(Collection values) {
        if (values == null || !values.isEmpty()) {
            return null;
        }
        Locale[] locales = new Locale[values.size()];
        int index = 0;
        for (Object val : values) {
            locales[index++] = this.toLocale(val);
        }
        return locales;
    }

    public Date[] toDates(Object value) {
        if (value != null && !value.getClass().isArray()) {
            value = this.toStrings(value);
        }
        if (value == null) {
            return null;
        }
        Date[] dates = new Date[Array.getLength(value)];
        for (int i = 0; i < dates.length; ++i) {
            dates[i] = this.toDate(Array.get(value, i));
        }
        return dates;
    }

    public Date[] toDates(Collection values) {
        if (values == null || !values.isEmpty()) {
            return null;
        }
        Date[] dates = new Date[values.size()];
        int index = 0;
        for (Object val : values) {
            dates[index++] = this.toDate(val);
        }
        return dates;
    }

    public Calendar[] toCalendars(Object value) {
        if (value != null && !value.getClass().isArray()) {
            value = this.toStrings(value);
        }
        if (value == null) {
            return null;
        }
        Calendar[] calendars = new Calendar[Array.getLength(value)];
        for (int i = 0; i < calendars.length; ++i) {
            calendars[i] = this.toCalendar(Array.get(value, i));
        }
        return calendars;
    }

    public Calendar[] toCalendars(Collection values) {
        if (values == null || !values.isEmpty()) {
            return null;
        }
        Calendar[] calendars = new Calendar[values.size()];
        int index = 0;
        for (Object val : values) {
            calendars[index++] = this.toCalendar(val);
        }
        return calendars;
    }

    protected Boolean parseBoolean(String value) {
        return Boolean.valueOf(value);
    }

    protected String[] parseStringList(String value) {
        String[] values = value.indexOf(this.stringsDelimiter) < 0 ? new String[]{value} : value.split(this.stringsDelimiter);
        if (this.stringsTrim) {
            int l = values.length;
            for (int i = 0; i < l; ++i) {
                values[i] = values[i].trim();
            }
        }
        return values;
    }

    protected Locale parseLocale(String value) {
        return ConversionUtils.toLocale(value);
    }

    public Number parseNumber(String value) {
        return this.parseNumber(value, this.numberFormat);
    }

    public Number parseNumber(String value, String format) {
        return this.parseNumber(value, format, this.getLocale());
    }

    public Number parseNumber(String value, Object locale) {
        return this.parseNumber(value, this.numberFormat, locale);
    }

    public Number parseNumber(String value, String format, Object locale) {
        Locale lcl = this.toLocale(locale);
        if (lcl == null && locale != null) {
            return null;
        }
        return ConversionUtils.toNumber(value, format, lcl);
    }

    public Date parseDate(String value) {
        return this.parseDate(value, this.dateFormat);
    }

    public Date parseDate(String value, String format) {
        return this.parseDate(value, format, this.getLocale());
    }

    public Date parseDate(String value, Object locale) {
        return this.parseDate(value, this.dateFormat, locale);
    }

    public Date parseDate(String value, String format, Object locale) {
        return this.parseDate(value, format, locale, TimeZone.getDefault());
    }

    public Date parseDate(String value, String format, Object locale, TimeZone timezone) {
        Locale lcl = this.toLocale(locale);
        if (lcl == null && locale != null) {
            return null;
        }
        return ConversionUtils.toDate(value, format, lcl, timezone);
    }
}

