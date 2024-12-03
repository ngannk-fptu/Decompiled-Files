/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

public final class StringConverter
extends AbstractConverter {
    public StringConverter() {
    }

    public StringConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return String.class;
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (String.class.equals(type) || Object.class.equals(type)) {
            return type.cast(value.toString());
        }
        throw this.conversionException(type, value);
    }
}

