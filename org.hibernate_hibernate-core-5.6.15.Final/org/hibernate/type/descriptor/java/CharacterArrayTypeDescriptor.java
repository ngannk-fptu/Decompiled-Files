/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.util.Arrays;
import java.util.Comparator;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ArrayMutabilityPlan;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.IncomparableComparator;

public class CharacterArrayTypeDescriptor
extends AbstractTypeDescriptor<Character[]> {
    public static final CharacterArrayTypeDescriptor INSTANCE = new CharacterArrayTypeDescriptor();

    public CharacterArrayTypeDescriptor() {
        super(Character[].class, ArrayMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Character[] value) {
        return new String(this.unwrapChars(value));
    }

    @Override
    public Character[] fromString(String string) {
        return this.wrapChars(string.toCharArray());
    }

    @Override
    public boolean areEqual(Character[] one, Character[] another) {
        return one == another || one != null && another != null && Arrays.equals((Object[])one, (Object[])another);
    }

    @Override
    public int extractHashCode(Character[] chars) {
        int hashCode = 1;
        for (Character aChar : chars) {
            hashCode = 31 * hashCode + aChar.charValue();
        }
        return hashCode;
    }

    @Override
    public Comparator<Character[]> getComparator() {
        return IncomparableComparator.INSTANCE;
    }

    @Override
    public <X> X unwrap(Character[] value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Character[].class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)new String(this.unwrapChars(value));
        }
        if (Clob.class.isAssignableFrom(type)) {
            return (X)options.getLobCreator().createClob(new String(this.unwrapChars(value)));
        }
        if (Reader.class.isAssignableFrom(type)) {
            return (X)new StringReader(new String(this.unwrapChars(value)));
        }
        if (CharacterStream.class.isAssignableFrom(type)) {
            return (X)new CharacterStreamImpl(new String(this.unwrapChars(value)));
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Character[] wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Character[].class.isInstance(value)) {
            return (Character[])value;
        }
        if (String.class.isInstance(value)) {
            return this.wrapChars(((String)value).toCharArray());
        }
        if (Clob.class.isInstance(value)) {
            return this.wrapChars(DataHelper.extractString((Clob)value).toCharArray());
        }
        if (Reader.class.isInstance(value)) {
            return this.wrapChars(DataHelper.extractString((Reader)value).toCharArray());
        }
        throw this.unknownWrap(value.getClass());
    }

    private Character[] wrapChars(char[] chars) {
        if (chars == null) {
            return null;
        }
        Character[] result = new Character[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            result[i] = Character.valueOf(chars[i]);
        }
        return result;
    }

    private char[] unwrapChars(Character[] chars) {
        if (chars == null) {
            return null;
        }
        char[] result = new char[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            result[i] = chars[i].charValue();
        }
        return result;
    }
}

