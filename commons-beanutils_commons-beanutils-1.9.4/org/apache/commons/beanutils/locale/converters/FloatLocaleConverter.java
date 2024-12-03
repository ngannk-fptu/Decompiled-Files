/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.converters.DecimalLocaleConverter;

public class FloatLocaleConverter
extends DecimalLocaleConverter {
    public FloatLocaleConverter() {
        this(false);
    }

    public FloatLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public FloatLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public FloatLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public FloatLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public FloatLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public FloatLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public FloatLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public FloatLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public FloatLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public FloatLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public FloatLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        double posDouble;
        Number parsed = (Number)super.parse(value, pattern);
        double doubleValue = parsed.doubleValue();
        double d = posDouble = doubleValue >= 0.0 ? doubleValue : doubleValue * -1.0;
        if (posDouble != 0.0 && (posDouble < (double)1.4E-45f || posDouble > 3.4028234663852886E38)) {
            throw new ConversionException("Supplied number is not of type Float: " + parsed);
        }
        return new Float(parsed.floatValue());
    }
}

