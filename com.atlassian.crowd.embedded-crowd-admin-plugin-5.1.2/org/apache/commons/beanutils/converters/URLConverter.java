/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.net.URL;
import org.apache.commons.beanutils.converters.AbstractConverter;

public final class URLConverter
extends AbstractConverter {
    public URLConverter() {
    }

    public URLConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return URL.class;
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (URL.class.equals(type)) {
            return type.cast(new URL(value.toString()));
        }
        throw this.conversionException(type, value);
    }
}

