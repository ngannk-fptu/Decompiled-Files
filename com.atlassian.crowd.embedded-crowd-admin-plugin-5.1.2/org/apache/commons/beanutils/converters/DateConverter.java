/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.Date;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public final class DateConverter
extends DateTimeConverter {
    public DateConverter() {
    }

    public DateConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Date.class;
    }
}

