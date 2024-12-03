/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.util.Comparator;
import java.util.Locale;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class LocaleTypeDescriptor
extends AbstractTypeDescriptor<Locale> {
    public static final LocaleTypeDescriptor INSTANCE = new LocaleTypeDescriptor();

    public LocaleTypeDescriptor() {
        super(Locale.class);
    }

    @Override
    public Comparator<Locale> getComparator() {
        return LocaleComparator.INSTANCE;
    }

    @Override
    public String toString(Locale value) {
        return value.toString();
    }

    @Override
    public Locale fromString(String string) {
        if (string == null) {
            return null;
        }
        if (string.isEmpty()) {
            return Locale.ROOT;
        }
        boolean separatorFound = false;
        int position = 0;
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] != '_') continue;
            if (separatorFound) {
                if (chars.length > i + 1) {
                    return new Locale(string, new String(chars, position, i - position), new String(chars, i + 1, chars.length - i - 1));
                }
                return new Locale(string, new String(chars, position, i - position), "");
            }
            string = new String(chars, position, i - position);
            position = i + 1;
            separatorFound = true;
        }
        if (!separatorFound) {
            return new Locale(string);
        }
        return new Locale(string, new String(chars, position, chars.length - position));
    }

    @Override
    public <X> X unwrap(Locale value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value.toString();
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Locale wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return this.fromString((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class LocaleComparator
    implements Comparator<Locale> {
        public static final LocaleComparator INSTANCE = new LocaleComparator();

        @Override
        public int compare(Locale o1, Locale o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }
}

