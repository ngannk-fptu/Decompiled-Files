/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class DoubleTypeDescriptor
extends AbstractTypeDescriptor<Double> {
    public static final DoubleTypeDescriptor INSTANCE = new DoubleTypeDescriptor();

    public DoubleTypeDescriptor() {
        super(Double.class);
    }

    @Override
    public String toString(Double value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Double fromString(String string) {
        return Double.valueOf(string);
    }

    @Override
    public <X> X unwrap(Double value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Double.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Byte.class.isAssignableFrom(type)) {
            return (X)Byte.valueOf(value.byteValue());
        }
        if (Short.class.isAssignableFrom(type)) {
            return (X)Short.valueOf(value.shortValue());
        }
        if (Integer.class.isAssignableFrom(type)) {
            return (X)Integer.valueOf(value.intValue());
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(value.longValue());
        }
        if (Float.class.isAssignableFrom(type)) {
            return (X)Float.valueOf(value.floatValue());
        }
        if (BigInteger.class.isAssignableFrom(type)) {
            return (X)BigInteger.valueOf(value.longValue());
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return (X)BigDecimal.valueOf(value);
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value.toString();
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Double wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Double.class.isInstance(value)) {
            return (Double)value;
        }
        if (Number.class.isInstance(value)) {
            return ((Number)value).doubleValue();
        }
        if (String.class.isInstance(value)) {
            return Double.valueOf((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

