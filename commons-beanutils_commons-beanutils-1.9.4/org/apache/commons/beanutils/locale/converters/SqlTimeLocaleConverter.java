/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

public class SqlTimeLocaleConverter
extends DateLocaleConverter {
    public SqlTimeLocaleConverter() {
        this(false);
    }

    public SqlTimeLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public SqlTimeLocaleConverter(Locale locale) {
        this(locale, false);
    }

    public SqlTimeLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null, locPattern);
    }

    public SqlTimeLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public SqlTimeLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public SqlTimeLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public SqlTimeLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), false);
    }

    public SqlTimeLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public SqlTimeLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public SqlTimeLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public SqlTimeLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        return new Time(((Date)super.parse(value, pattern)).getTime());
    }
}

