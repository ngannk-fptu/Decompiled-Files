/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.NumberConverter;

public final class DoubleConverter
extends NumberConverter {
    public DoubleConverter() {
        super(true);
    }

    public DoubleConverter(Object defaultValue) {
        super(true, defaultValue);
    }

    protected Class<Double> getDefaultType() {
        return Double.class;
    }
}

