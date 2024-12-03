/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class BooleanTypeDescriptor
extends AbstractTypeDescriptor<Boolean> {
    public static final BooleanTypeDescriptor INSTANCE = new BooleanTypeDescriptor();
    private final char characterValueTrue;
    private final char characterValueFalse;
    private final char characterValueTrueLC;
    private final String stringValueTrue;
    private final String stringValueFalse;

    public BooleanTypeDescriptor() {
        this('Y', 'N');
    }

    public BooleanTypeDescriptor(char characterValueTrue, char characterValueFalse) {
        super(Boolean.class);
        this.characterValueTrue = Character.toUpperCase(characterValueTrue);
        this.characterValueFalse = Character.toUpperCase(characterValueFalse);
        this.characterValueTrueLC = Character.toLowerCase(characterValueTrue);
        this.stringValueTrue = String.valueOf(characterValueTrue);
        this.stringValueFalse = String.valueOf(characterValueFalse);
    }

    @Override
    public String toString(Boolean value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Boolean fromString(String string) {
        return Boolean.valueOf(string);
    }

    @Override
    public <X> X unwrap(Boolean value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Byte.class.isAssignableFrom(type)) {
            return (X)this.toByte(value);
        }
        if (Short.class.isAssignableFrom(type)) {
            return (X)this.toShort(value);
        }
        if (Integer.class.isAssignableFrom(type)) {
            return (X)this.toInteger(value);
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)this.toInteger(value);
        }
        if (Character.class.isAssignableFrom(type)) {
            return (X)Character.valueOf(value != false ? this.characterValueTrue : this.characterValueFalse);
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)(value != false ? this.stringValueTrue : this.stringValueFalse);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Boolean wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Boolean.class.isInstance(value)) {
            return (Boolean)value;
        }
        if (Number.class.isInstance(value)) {
            int intValue = ((Number)value).intValue();
            return intValue == 0 ? Boolean.FALSE : Boolean.TRUE;
        }
        if (Character.class.isInstance(value)) {
            return this.isTrue(((Character)value).charValue()) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (String.class.isInstance(value)) {
            return this.isTrue((String)value) ? Boolean.TRUE : Boolean.FALSE;
        }
        throw this.unknownWrap(value.getClass());
    }

    private boolean isTrue(String strValue) {
        if (strValue != null && !strValue.isEmpty()) {
            return this.isTrue(strValue.charAt(0));
        }
        return false;
    }

    private boolean isTrue(char charValue) {
        return charValue == this.characterValueTrue || charValue == this.characterValueTrueLC;
    }

    public int toInt(Boolean value) {
        return value != false ? 1 : 0;
    }

    public Byte toByte(Boolean value) {
        return (byte)this.toInt(value);
    }

    public Short toShort(Boolean value) {
        return (short)this.toInt(value);
    }

    public Integer toInteger(Boolean value) {
        return this.toInt(value);
    }

    public Long toLong(Boolean value) {
        return this.toInt(value);
    }
}

