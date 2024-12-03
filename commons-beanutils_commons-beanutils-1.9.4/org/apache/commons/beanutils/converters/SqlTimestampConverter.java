/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public final class SqlTimestampConverter
extends DateTimeConverter {
    public SqlTimestampConverter() {
    }

    public SqlTimestampConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Timestamp.class;
    }

    @Override
    protected DateFormat getFormat(Locale locale, TimeZone timeZone) {
        DateFormat format = null;
        format = locale == null ? DateFormat.getDateTimeInstance(3, 3) : DateFormat.getDateTimeInstance(3, 3, locale);
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }
}

