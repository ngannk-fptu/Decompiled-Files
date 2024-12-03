/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.sql.Date;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public final class SqlDateConverter
extends DateTimeConverter {
    public SqlDateConverter() {
    }

    public SqlDateConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Date.class;
    }
}

