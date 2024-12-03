/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.math.BigDecimal;
import org.apache.commons.beanutils.converters.NumberConverter;

public final class BigDecimalConverter
extends NumberConverter {
    public BigDecimalConverter() {
        super(true);
    }

    public BigDecimalConverter(Object defaultValue) {
        super(true, defaultValue);
    }

    protected Class<BigDecimal> getDefaultType() {
        return BigDecimal.class;
    }
}

