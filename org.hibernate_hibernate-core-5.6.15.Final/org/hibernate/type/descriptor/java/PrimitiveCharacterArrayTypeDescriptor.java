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

public class PrimitiveCharacterArrayTypeDescriptor
extends AbstractTypeDescriptor<char[]> {
    public static final PrimitiveCharacterArrayTypeDescriptor INSTANCE = new PrimitiveCharacterArrayTypeDescriptor();

    protected PrimitiveCharacterArrayTypeDescriptor() {
        super(char[].class, ArrayMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(char[] value) {
        return new String(value);
    }

    @Override
    public char[] fromString(String string) {
        return string.toCharArray();
    }

    @Override
    public boolean areEqual(char[] one, char[] another) {
        return one == another || one != null && another != null && Arrays.equals(one, another);
    }

    @Override
    public int extractHashCode(char[] chars) {
        int hashCode = 1;
        for (char aChar : chars) {
            hashCode = 31 * hashCode + aChar;
        }
        return hashCode;
    }

    @Override
    public Comparator<char[]> getComparator() {
        return IncomparableComparator.INSTANCE;
    }

    @Override
    public <X> X unwrap(char[] value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (char[].class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)new String(value);
        }
        if (Clob.class.isAssignableFrom(type)) {
            return (X)options.getLobCreator().createClob(new String(value));
        }
        if (Reader.class.isAssignableFrom(type)) {
            return (X)new StringReader(new String(value));
        }
        if (CharacterStream.class.isAssignableFrom(type)) {
            return (X)new CharacterStreamImpl(new String(value));
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> char[] wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (char[].class.isInstance(value)) {
            return (char[])value;
        }
        if (String.class.isInstance(value)) {
            return ((String)value).toCharArray();
        }
        if (Clob.class.isInstance(value)) {
            return DataHelper.extractString((Clob)value).toCharArray();
        }
        if (Reader.class.isInstance(value)) {
            return DataHelper.extractString((Reader)value).toCharArray();
        }
        throw this.unknownWrap(value.getClass());
    }
}

