/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.NumberConverter;

public final class ShortConverter
extends NumberConverter {
    public ShortConverter() {
        super(false);
    }

    public ShortConverter(Object defaultValue) {
        super(false, defaultValue);
    }

    protected Class<Short> getDefaultType() {
        return Short.class;
    }
}

