/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;

public class GenericTypeValidator
implements Serializable {
    private static final long serialVersionUID = 5487162314134261703L;
    private static final Log LOG = LogFactory.getLog(GenericTypeValidator.class);

    public static Byte formatByte(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Byte.valueOf(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Byte formatByte(String value, Locale locale) {
        Byte result = null;
        if (value != null) {
            NumberFormat formatter = null;
            formatter = locale != null ? NumberFormat.getNumberInstance(locale) : NumberFormat.getNumberInstance(Locale.getDefault());
            formatter.setParseIntegerOnly(true);
            ParsePosition pos = new ParsePosition(0);
            Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -128.0 && num.doubleValue() <= 127.0) {
                result = num.byteValue();
            }
        }
        return result;
    }

    public static Short formatShort(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Short.valueOf(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Short formatShort(String value, Locale locale) {
        Short result = null;
        if (value != null) {
            NumberFormat formatter = null;
            formatter = locale != null ? NumberFormat.getNumberInstance(locale) : NumberFormat.getNumberInstance(Locale.getDefault());
            formatter.setParseIntegerOnly(true);
            ParsePosition pos = new ParsePosition(0);
            Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -32768.0 && num.doubleValue() <= 32767.0) {
                result = num.shortValue();
            }
        }
        return result;
    }

    public static Integer formatInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer formatInt(String value, Locale locale) {
        Integer result = null;
        if (value != null) {
            NumberFormat formatter = null;
            formatter = locale != null ? NumberFormat.getNumberInstance(locale) : NumberFormat.getNumberInstance(Locale.getDefault());
            formatter.setParseIntegerOnly(true);
            ParsePosition pos = new ParsePosition(0);
            Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -2.147483648E9 && num.doubleValue() <= 2.147483647E9) {
                result = num.intValue();
            }
        }
        return result;
    }

    public static Long formatLong(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long formatLong(String value, Locale locale) {
        Long result = null;
        if (value != null) {
            NumberFormat formatter = null;
            formatter = locale != null ? NumberFormat.getNumberInstance(locale) : NumberFormat.getNumberInstance(Locale.getDefault());
            formatter.setParseIntegerOnly(true);
            ParsePosition pos = new ParsePosition(0);
            Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -9.223372036854776E18 && num.doubleValue() <= 9.223372036854776E18) {
                result = num.longValue();
            }
        }
        return result;
    }

    public static Float formatFloat(String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Float(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Float formatFloat(String value, Locale locale) {
        Float result = null;
        if (value != null) {
            NumberFormat formatter = null;
            formatter = locale != null ? NumberFormat.getInstance(locale) : NumberFormat.getInstance(Locale.getDefault());
            ParsePosition pos = new ParsePosition(0);
            Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -3.4028234663852886E38 && num.doubleValue() <= 3.4028234663852886E38) {
                result = new Float(num.floatValue());
            }
        }
        return result;
    }

    public static Double formatDouble(String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Double(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double formatDouble(String value, Locale locale) {
        Double result = null;
        if (value != null) {
            NumberFormat formatter = null;
            formatter = locale != null ? NumberFormat.getInstance(locale) : NumberFormat.getInstance(Locale.getDefault());
            ParsePosition pos = new ParsePosition(0);
            Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -1.7976931348623157E308 && num.doubleValue() <= Double.MAX_VALUE) {
                result = new Double(num.doubleValue());
            }
        }
        return result;
    }

    public static Date formatDate(String value, Locale locale) {
        Date date;
        block7: {
            date = null;
            if (value == null) {
                return null;
            }
            try {
                DateFormat formatterShort = null;
                DateFormat formatterDefault = null;
                if (locale != null) {
                    formatterShort = DateFormat.getDateInstance(3, locale);
                    formatterDefault = DateFormat.getDateInstance(2, locale);
                } else {
                    formatterShort = DateFormat.getDateInstance(3, Locale.getDefault());
                    formatterDefault = DateFormat.getDateInstance(2, Locale.getDefault());
                }
                formatterShort.setLenient(false);
                formatterDefault.setLenient(false);
                try {
                    date = formatterShort.parse(value);
                }
                catch (ParseException e) {
                    date = formatterDefault.parse(value);
                }
            }
            catch (ParseException e) {
                if (!LOG.isDebugEnabled()) break block7;
                LOG.debug((Object)("Date parse failed value=[" + value + "], " + "locale=[" + locale + "] " + e));
            }
        }
        return date;
    }

    public static Date formatDate(String value, String datePattern, boolean strict) {
        Date date;
        block4: {
            date = null;
            if (value == null || datePattern == null || datePattern.length() == 0) {
                return null;
            }
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
                formatter.setLenient(false);
                date = formatter.parse(value);
                if (strict && datePattern.length() != value.length()) {
                    date = null;
                }
            }
            catch (ParseException e) {
                if (!LOG.isDebugEnabled()) break block4;
                LOG.debug((Object)("Date parse failed value=[" + value + "], " + "pattern=[" + datePattern + "], " + "strict=[" + strict + "] " + e));
            }
        }
        return date;
    }

    public static Long formatCreditCard(String value) {
        return GenericValidator.isCreditCard(value) ? Long.valueOf(value) : null;
    }
}

