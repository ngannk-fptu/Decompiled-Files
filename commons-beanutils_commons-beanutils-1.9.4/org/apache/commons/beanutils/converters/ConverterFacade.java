/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.Converter;

public final class ConverterFacade
implements Converter {
    private final Converter converter;

    public ConverterFacade(Converter converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter is missing");
        }
        this.converter = converter;
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        return this.converter.convert(type, value);
    }

    public String toString() {
        return "ConverterFacade[" + this.converter.toString() + "]";
    }
}

