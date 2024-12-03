/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils.locale.converters;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DecimalLocaleConverter
extends BaseLocaleConverter {
    private final Log log = LogFactory.getLog(DecimalLocaleConverter.class);

    public DecimalLocaleConverter() {
        this(false);
    }

    public DecimalLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public DecimalLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public DecimalLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public DecimalLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public DecimalLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public DecimalLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public DecimalLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public DecimalLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public DecimalLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public DecimalLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public DecimalLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        if (value instanceof Number) {
            return value;
        }
        DecimalFormat formatter = (DecimalFormat)DecimalFormat.getInstance(this.locale);
        if (pattern != null) {
            if (this.locPattern) {
                formatter.applyLocalizedPattern(pattern);
            } else {
                formatter.applyPattern(pattern);
            }
        } else {
            this.log.debug((Object)"No pattern provided, using default.");
        }
        return formatter.parse((String)value);
    }
}

