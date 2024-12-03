/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public final class SqlTimeConverter
extends DateTimeConverter {
    public SqlTimeConverter() {
    }

    public SqlTimeConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Time.class;
    }

    @Override
    protected DateFormat getFormat(Locale locale, TimeZone timeZone) {
        DateFormat format = null;
        format = locale == null ? DateFormat.getTimeInstance(3) : DateFormat.getTimeInstance(3, locale);
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }
}

