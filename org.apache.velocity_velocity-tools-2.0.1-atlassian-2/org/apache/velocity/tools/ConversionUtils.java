/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.velocity.tools.ClassUtils;

public class ConversionUtils {
    public static final ConversionUtils INSTANCE = new ConversionUtils();
    private static final int STYLE_NUMBER = 0;
    private static final int STYLE_CURRENCY = 1;
    private static final int STYLE_PERCENT = 2;
    private static final int STYLE_INTEGER = 4;
    private static ConcurrentMap<String, NumberFormat> customFormatsCache = new ConcurrentHashMap<String, NumberFormat>();

    private ConversionUtils() {
    }

    public ConversionUtils getInstance() {
        return INSTANCE;
    }

    public static NumberFormat getNumberFormat(String format, Locale locale) {
        if (format == null || locale == null) {
            return null;
        }
        NumberFormat nf = null;
        int style = ConversionUtils.getNumberStyleAsInt(format);
        if (style < 0) {
            String cacheKey = format + "%" + locale.toString();
            nf = (NumberFormat)customFormatsCache.get(cacheKey);
            if (nf == null) {
                nf = new DecimalFormat(format, new DecimalFormatSymbols(locale));
                customFormatsCache.put(cacheKey, nf);
            }
        } else {
            nf = ConversionUtils.getNumberFormat(style, locale);
        }
        return nf;
    }

    public static NumberFormat getNumberFormat(int numberStyle, Locale locale) {
        try {
            NumberFormat nf;
            switch (numberStyle) {
                case 0: {
                    nf = NumberFormat.getNumberInstance(locale);
                    break;
                }
                case 1: {
                    nf = NumberFormat.getCurrencyInstance(locale);
                    break;
                }
                case 2: {
                    nf = NumberFormat.getPercentInstance(locale);
                    break;
                }
                case 4: {
                    nf = NumberFormat.getIntegerInstance(locale);
                    break;
                }
                default: {
                    nf = null;
                }
            }
            return nf;
        }
        catch (Exception suppressed) {
            return null;
        }
    }

    public static int getNumberStyleAsInt(String style) {
        if (style == null || style.length() < 6 || style.length() > 8) {
            return -1;
        }
        if (style.equalsIgnoreCase("default")) {
            return 0;
        }
        if (style.equalsIgnoreCase("number")) {
            return 0;
        }
        if (style.equalsIgnoreCase("currency")) {
            return 1;
        }
        if (style.equalsIgnoreCase("percent")) {
            return 2;
        }
        if (style.equalsIgnoreCase("integer")) {
            return 4;
        }
        return -1;
    }

    public static Number toNumber(Object obj) {
        return ConversionUtils.toNumber(obj, true);
    }

    public static Number toNumber(Object obj, boolean handleStrings) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return (Number)obj;
        }
        if (obj instanceof Date) {
            return ((Date)obj).getTime();
        }
        if (obj instanceof Calendar) {
            Date date = ((Calendar)obj).getTime();
            return date.getTime();
        }
        if (handleStrings) {
            return ConversionUtils.toNumber(obj.toString(), "default", Locale.getDefault());
        }
        return null;
    }

    public static Number toNumber(String value, String format, Locale locale) {
        if (value == null || format == null || locale == null) {
            return null;
        }
        try {
            NumberFormat parser = ConversionUtils.getNumberFormat(format, locale);
            return parser.parse(value);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Number toNumber(Object value, String format, Locale locale) {
        Number number = ConversionUtils.toNumber(value, false);
        if (number != null) {
            return number;
        }
        return ConversionUtils.toNumber(String.valueOf(value), format, locale);
    }

    public static DateFormat getDateFormat(String format, Locale locale, TimeZone timezone) {
        if (format == null) {
            return null;
        }
        DateFormat df = null;
        if (format.endsWith("_date")) {
            String fmt = format.substring(0, format.length() - 5);
            int style = ConversionUtils.getDateStyleAsInt(fmt);
            df = ConversionUtils.getDateFormat(style, -1, locale, timezone);
        } else if (format.endsWith("_time")) {
            String fmt = format.substring(0, format.length() - 5);
            int style = ConversionUtils.getDateStyleAsInt(fmt);
            df = ConversionUtils.getDateFormat(-1, style, locale, timezone);
        } else {
            int style = ConversionUtils.getDateStyleAsInt(format);
            if (style < 0) {
                df = new SimpleDateFormat(format, locale);
                df.setTimeZone(timezone);
            } else {
                df = ConversionUtils.getDateFormat(style, style, locale, timezone);
            }
        }
        return df;
    }

    public static DateFormat getDateFormat(String dateStyle, String timeStyle, Locale locale, TimeZone timezone) {
        int ds = ConversionUtils.getDateStyleAsInt(dateStyle);
        int ts = ConversionUtils.getDateStyleAsInt(timeStyle);
        return ConversionUtils.getDateFormat(ds, ts, locale, timezone);
    }

    public static DateFormat getDateFormat(int dateStyle, int timeStyle, Locale locale, TimeZone timezone) {
        try {
            DateFormat df = dateStyle < 0 && timeStyle < 0 ? DateFormat.getInstance() : (timeStyle < 0 ? DateFormat.getDateInstance(dateStyle, locale) : (dateStyle < 0 ? DateFormat.getTimeInstance(timeStyle, locale) : DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale)));
            df.setTimeZone(timezone);
            return df;
        }
        catch (Exception suppressed) {
            return null;
        }
    }

    public static int getDateStyleAsInt(String style) {
        if (style == null || style.length() < 4 || style.length() > 7) {
            return -1;
        }
        if (style.equalsIgnoreCase("full")) {
            return 0;
        }
        if (style.equalsIgnoreCase("long")) {
            return 1;
        }
        if (style.equalsIgnoreCase("medium")) {
            return 2;
        }
        if (style.equalsIgnoreCase("short")) {
            return 3;
        }
        if (style.equalsIgnoreCase("default")) {
            return 2;
        }
        return -1;
    }

    public static Date toDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date)obj;
        }
        if (obj instanceof Calendar) {
            return ((Calendar)obj).getTime();
        }
        if (obj instanceof Number) {
            Date d = new Date();
            d.setTime(((Number)obj).longValue());
            return d;
        }
        return null;
    }

    public static Date toDate(Object obj, String format, Locale locale, TimeZone timezone) {
        Date date = ConversionUtils.toDate(obj);
        if (date != null) {
            return date;
        }
        return ConversionUtils.toDate(String.valueOf(obj), format, locale, timezone);
    }

    public static Date toDate(String str, String format, Locale locale, TimeZone timezone) {
        try {
            DateFormat parser = ConversionUtils.getDateFormat(format, locale, timezone);
            return parser.parse(str);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Calendar toCalendar(Date date, Locale locale) {
        if (date == null) {
            return null;
        }
        Calendar cal = locale == null ? Calendar.getInstance() : Calendar.getInstance(locale);
        cal.setTime(date);
        cal.getTime();
        return cal;
    }

    public static String toString(Object value) {
        if (value instanceof String) {
            return (String)value;
        }
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                return ConversionUtils.toString(Array.get(value, 0));
            }
            return null;
        }
        return String.valueOf(value);
    }

    public static String toString(Collection values) {
        if (values != null && !values.isEmpty()) {
            return ConversionUtils.toString(values.iterator().next());
        }
        return null;
    }

    public static Boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        String s = ConversionUtils.toString(value);
        return s != null ? Boolean.valueOf(s) : null;
    }

    public static Locale toLocale(String value) {
        if (value.indexOf(95) < 0) {
            return new Locale(value);
        }
        String[] params = value.split("_");
        if (params.length == 2) {
            return new Locale(params[0], params[1]);
        }
        if (params.length == 3) {
            return new Locale(params[0], params[1], params[2]);
        }
        return null;
    }

    public static URL toURL(String value) {
        return ConversionUtils.toURL(value, ConversionUtils.class);
    }

    public static URL toURL(String value, Object caller) {
        try {
            File file = new File(value);
            if (file.exists()) {
                return file.toURI().toURL();
            }
        }
        catch (Exception file) {
            // empty catch block
        }
        try {
            URL url = ClassUtils.getResource(value, caller);
            if (url != null) {
                return url;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            return new URL(value);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

