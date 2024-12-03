/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.converters.DecimalLocaleConverter;

public class BigIntegerLocaleConverter
extends DecimalLocaleConverter {
    public BigIntegerLocaleConverter() {
        this(false);
    }

    public BigIntegerLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public BigIntegerLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public BigIntegerLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public BigIntegerLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public BigIntegerLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public BigIntegerLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public BigIntegerLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public BigIntegerLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public BigIntegerLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public BigIntegerLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public BigIntegerLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        Object result = super.parse(value, pattern);
        if (result == null || result instanceof BigInteger) {
            return result;
        }
        if (result instanceof Number) {
            return BigInteger.valueOf(((Number)result).longValue());
        }
        try {
            return new BigInteger(result.toString());
        }
        catch (NumberFormatException ex) {
            throw new ConversionException("Suplied number is not of type BigInteger: " + result);
        }
    }
}

