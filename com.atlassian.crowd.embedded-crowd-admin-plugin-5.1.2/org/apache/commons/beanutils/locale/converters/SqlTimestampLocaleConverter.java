/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale.converters;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

public class SqlTimestampLocaleConverter
extends DateLocaleConverter {
    public SqlTimestampLocaleConverter() {
        this(false);
    }

    public SqlTimestampLocaleConverter(boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }

    public SqlTimestampLocaleConverter(Locale locale) {
        this(locale, (String)null);
    }

    public SqlTimestampLocaleConverter(Locale locale, boolean locPattern) {
        this(locale, (String)null);
    }

    public SqlTimestampLocaleConverter(Locale locale, String pattern) {
        this(locale, pattern, false);
    }

    public SqlTimestampLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        super(locale, pattern, locPattern);
    }

    public SqlTimestampLocaleConverter(Object defaultValue) {
        this(defaultValue, false);
    }

    public SqlTimestampLocaleConverter(Object defaultValue, boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }

    public SqlTimestampLocaleConverter(Object defaultValue, Locale locale) {
        this(defaultValue, locale, false);
    }

    public SqlTimestampLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }

    public SqlTimestampLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    public SqlTimestampLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }

    @Override
    protected Object parse(Object value, String pattern) throws ParseException {
        return new Timestamp(((Date)super.parse(value, pattern)).getTime());
    }
}

