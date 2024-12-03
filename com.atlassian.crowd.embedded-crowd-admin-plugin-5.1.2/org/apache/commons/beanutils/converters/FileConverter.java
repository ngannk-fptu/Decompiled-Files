/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.io.File;
import org.apache.commons.beanutils.converters.AbstractConverter;

public final class FileConverter
extends AbstractConverter {
    public FileConverter() {
    }

    public FileConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return File.class;
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (File.class.equals(type)) {
            return type.cast(new File(value.toString()));
        }
        throw this.conversionException(type, value);
    }
}

