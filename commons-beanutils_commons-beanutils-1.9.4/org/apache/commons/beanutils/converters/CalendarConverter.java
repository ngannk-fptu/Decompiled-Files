/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.Calendar;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public final class CalendarConverter
extends DateTimeConverter {
    public CalendarConverter() {
    }

    public CalendarConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Calendar.class;
    }
}

