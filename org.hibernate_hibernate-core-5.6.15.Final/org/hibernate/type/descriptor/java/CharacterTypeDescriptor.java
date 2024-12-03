/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class CharacterTypeDescriptor
extends AbstractTypeDescriptor<Character> {
    public static final CharacterTypeDescriptor INSTANCE = new CharacterTypeDescriptor();

    public CharacterTypeDescriptor() {
        super(Character.class);
    }

    @Override
    public String toString(Character value) {
        return value.toString();
    }

    @Override
    public Character fromString(String string) {
        if (string.length() != 1) {
            throw new HibernateException("multiple or zero characters found parsing string");
        }
        return Character.valueOf(string.charAt(0));
    }

    @Override
    public <X> X unwrap(Character value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Character.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value.toString();
        }
        if (Number.class.isAssignableFrom(type)) {
            return (X)Short.valueOf((short)value.charValue());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Character wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Character.class.isInstance(value)) {
            return (Character)value;
        }
        if (String.class.isInstance(value)) {
            String str = (String)value;
            return Character.valueOf(str.charAt(0));
        }
        if (Number.class.isInstance(value)) {
            Number nbr = (Number)value;
            return Character.valueOf((char)nbr.shortValue());
        }
        throw this.unknownWrap(value.getClass());
    }
}

