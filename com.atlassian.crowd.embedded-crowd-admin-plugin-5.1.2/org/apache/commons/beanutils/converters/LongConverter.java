/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.NumberConverter;

public final class LongConverter
extends NumberConverter {
    public LongConverter() {
        super(false);
    }

    public LongConverter(Object defaultValue) {
        super(false, defaultValue);
    }

    protected Class<Long> getDefaultType() {
        return Long.class;
    }
}

