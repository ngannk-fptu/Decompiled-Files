/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.math.BigInteger;
import org.apache.commons.beanutils.converters.NumberConverter;

public final class BigIntegerConverter
extends NumberConverter {
    public BigIntegerConverter() {
        super(false);
    }

    public BigIntegerConverter(Object defaultValue) {
        super(false, defaultValue);
    }

    protected Class<BigInteger> getDefaultType() {
        return BigInteger.class;
    }
}

