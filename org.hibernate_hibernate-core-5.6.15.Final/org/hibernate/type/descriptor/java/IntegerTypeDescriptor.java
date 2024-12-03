/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class IntegerTypeDescriptor
extends AbstractTypeDescriptor<Integer> {
    public static final IntegerTypeDescriptor INSTANCE = new IntegerTypeDescriptor();

    public IntegerTypeDescriptor() {
        super(Integer.class);
    }

    @Override
    public String toString(Integer value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Integer fromString(String string) {
        return string == null ? null : Integer.valueOf(string);
    }

    @Override
    public <X> X unwrap(Integer value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Integer.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Byte.class.isAssignableFrom(type)) {
            return (X)Byte.valueOf(value.byteValue());
        }
        if (Short.class.isAssignableFrom(type)) {
            return (X)Short.valueOf(value.shortValue());
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(value.longValue());
        }
        if (Double.class.isAssignableFrom(type)) {
            return (X)Double.valueOf(value.doubleValue());
        }
        if (Float.class.isAssignableFrom(type)) {
            return (X)Float.valueOf(value.floatValue());
        }
        if (BigInteger.class.isAssignableFrom(type)) {
            return (X)BigInteger.valueOf(value.intValue());
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return (X)BigDecimal.valueOf(value.intValue());
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value.toString();
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Integer wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Integer.class.isInstance(value)) {
            return (Integer)value;
        }
        if (Number.class.isInstance(value)) {
            return ((Number)value).intValue();
        }
        if (String.class.isInstance(value)) {
            return Integer.valueOf((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

