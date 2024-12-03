/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

public final class ClassConverter
extends AbstractConverter {
    public ClassConverter() {
    }

    public ClassConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Class.class;
    }

    @Override
    protected String convertToString(Object value) {
        return value instanceof Class ? ((Class)value).getName() : value.toString();
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (Class.class.equals(type)) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                try {
                    return type.cast(classLoader.loadClass(value.toString()));
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
            classLoader = ClassConverter.class.getClassLoader();
            return type.cast(classLoader.loadClass(value.toString()));
        }
        throw this.conversionException(type, value);
    }
}

