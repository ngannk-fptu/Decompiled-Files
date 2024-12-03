/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils.locale.converters;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateLocaleConverter
extends BaseLocaleConverter {
    private final Log log = LogFactory.getLog(DateLocaleConverter.class);
    boolean isLenient = false;
    private static final String DEFAULT_PATTERN_CHARS = DateLocaleConverter.initDefaultChars();

    public DateLocaleConverter() {
        this(false);
    }

    public DateLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public DateLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public DateLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public DateLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public DateLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public DateLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public DateLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public DateLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public DateLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public DateLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public DateLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    public boolean isLenient() {
        return this.isLenient;
    }

    public void setLenient(boolean lenient) {
        this.isLenient = lenient;
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        if (value instanceof Date) {
            return value;
        }
        if (value instanceof Calendar) {
            return ((Calendar)value).getTime();
        }
        if (this.locPattern) {
            pattern = this.convertLocalizedPattern(pattern, this.locale);
        }
        DateFormat formatter = pattern == null ? DateFormat.getDateInstance(3, this.locale) : new SimpleDateFormat(pattern, this.locale);
        formatter.setLenient(this.isLenient);
        ParsePosition pos = new ParsePosition(0);
        String strValue = value.toString();
        Object parsedValue = formatter.parseObject(strValue, pos);
        if (pos.getErrorIndex() > -1) {
            throw new ConversionException("Error parsing date '" + value + "' at position=" + pos.getErrorIndex());
        }
        if (pos.getIndex() < strValue.length()) {
            throw new ConversionException("Date '" + value + "' contains unparsed characters from position=" + pos.getIndex());
        }
        return parsedValue;
    }

    private String convertLocalizedPattern(String localizedPattern, Locale locale) {
        if (localizedPattern == null) {
            return null;
        }
        DateFormatSymbols localizedSymbols = new DateFormatSymbols(locale);
        String localChars = localizedSymbols.getLocalPatternChars();
        if (DEFAULT_PATTERN_CHARS.equals(localChars)) {
            return localizedPattern;
        }
        String convertedPattern = null;
        try {
            convertedPattern = this.convertPattern(localizedPattern, localChars, DEFAULT_PATTERN_CHARS);
        }
        catch (Exception ex) {
            this.log.debug((Object)("Converting pattern '" + localizedPattern + "' for " + locale), (Throwable)ex);
        }
        return convertedPattern;
    }

    private String convertPattern(String pattern, String fromChars, String toChars) {
        StringBuilder converted = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < pattern.length(); ++i) {
            char thisChar = pattern.charAt(i);
            if (quoted) {
                if (thisChar == '\'') {
                    quoted = false;
                }
            } else if (thisChar == '\'') {
                quoted = true;
            } else if (thisChar >= 'a' && thisChar <= 'z' || thisChar >= 'A' && thisChar <= 'Z') {
                int index = fromChars.indexOf(thisChar);
                if (index == -1) {
                    throw new IllegalArgumentException("Illegal pattern character '" + thisChar + "'");
                }
                thisChar = toChars.charAt(index);
            }
            converted.append(thisChar);
        }
        if (quoted) {
            throw new IllegalArgumentException("Unfinished quote in pattern");
        }
        return converted.toString();
    }

    private static String initDefaultChars() {
        DateFormatSymbols defaultSymbols = new DateFormatSymbols(Locale.US);
        return defaultSymbols.getLocalPatternChars();
    }
}

