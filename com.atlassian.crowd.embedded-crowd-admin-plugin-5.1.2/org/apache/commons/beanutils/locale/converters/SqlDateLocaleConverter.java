/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

public class SqlDateLocaleConverter
extends DateLocaleConverter {
    public SqlDateLocaleConverter() {
        this(false);
    }

    public SqlDateLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public SqlDateLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public SqlDateLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public SqlDateLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public SqlDateLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public SqlDateLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public SqlDateLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public SqlDateLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public SqlDateLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public SqlDateLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public SqlDateLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        return new java.sql.Date(((Date)super.parse(value, pattern)).getTime());
    }
}

