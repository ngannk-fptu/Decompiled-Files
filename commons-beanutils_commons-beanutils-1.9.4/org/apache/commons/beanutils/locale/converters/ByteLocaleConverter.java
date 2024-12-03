/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.converters.DecimalLocaleConverter;

public class ByteLocaleConverter
extends DecimalLocaleConverter {
    public ByteLocaleConverter() {
        this(false);
    }

    public ByteLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public ByteLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public ByteLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public ByteLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public ByteLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public ByteLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public ByteLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public ByteLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public ByteLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public ByteLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public ByteLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        Number parsed = (Number)super.parse(value, pattern);
        if (parsed.longValue() != (long)parsed.byteValue()) {
            throw new ConversionException("Supplied number is not of type Byte: " + parsed.longValue());
        }
        return new Byte(parsed.byteValue());
    }
}

