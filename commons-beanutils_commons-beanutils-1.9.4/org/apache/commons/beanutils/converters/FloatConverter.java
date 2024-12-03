/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.NumberConverter;

public final class FloatConverter
extends NumberConverter {
    public FloatConverter() {
        super(true);
    }

    public FloatConverter(Object defaultValue) {
        super(true, defaultValue);
    }

    protected Class<Float> getDefaultType() {
        return Float.class;
    }
}

