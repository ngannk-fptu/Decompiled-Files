/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.NumberConverter;

public final class IntegerConverter
extends NumberConverter {
    public IntegerConverter() {
        super(false);
    }

    public IntegerConverter(Object defaultValue) {
        super(false, defaultValue);
    }

    protected Class<Integer> getDefaultType() {
        return Integer.class;
    }
}

