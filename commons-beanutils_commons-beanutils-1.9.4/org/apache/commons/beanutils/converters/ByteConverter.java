/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.NumberConverter;

public final class ByteConverter
extends NumberConverter {
    public ByteConverter() {
        super(false);
    }

    public ByteConverter(Object defaultValue) {
        super(false, defaultValue);
    }

    protected Class<Byte> getDefaultType() {
        return Byte.class;
    }
}

