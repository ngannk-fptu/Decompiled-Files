/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.converters.DecimalLocaleConverter;

public class IntegerLocaleConverter
extends DecimalLocaleConverter {
    public IntegerLocaleConverter() {
        this(false);
    }

    public IntegerLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public IntegerLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public IntegerLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public IntegerLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public IntegerLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public IntegerLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public IntegerLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public IntegerLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public IntegerLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public IntegerLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public IntegerLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        Number parsed = (Number)super.parse(value, pattern);
        if (parsed.longValue() != (long)parsed.intValue()) {
            throw new ConversionException("Suplied number is not of type Integer: " + parsed.longValue());
        }
        return new Integer(parsed.intValue());
    }
}

