/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class ByteTypeDescriptor
extends AbstractTypeDescriptor<Byte> {
    public static final ByteTypeDescriptor INSTANCE = new ByteTypeDescriptor();

    public ByteTypeDescriptor() {
        super(Byte.class);
    }

    @Override
    public String toString(Byte value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Byte fromString(String string) {
        return Byte.valueOf(string);
    }

    @Override
    public <X> X unwrap(Byte value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Byte.class.isAssignableFrom(type)) {
            return (X)value;
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
        if (Double.class.isAssignableFrom(type)) {
            return (X)Double.valueOf(value.doubleValue());
        }
        if (Float.class.isAssignableFrom(type)) {
            return (X)Float.valueOf(value.floatValue());
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value.toString();
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Byte wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Byte.class.isInstance(value)) {
            return (Byte)value;
        }
        if (Number.class.isInstance(value)) {
            return ((Number)value).byteValue();
        }
        if (String.class.isInstance(value)) {
            return Byte.valueOf((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

