/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.util.Currency;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class CurrencyTypeDescriptor
extends AbstractTypeDescriptor<Currency> {
    public static final CurrencyTypeDescriptor INSTANCE = new CurrencyTypeDescriptor();

    public CurrencyTypeDescriptor() {
        super(Currency.class);
    }

    @Override
    public String toString(Currency value) {
        return value.getCurrencyCode();
    }

    @Override
    public Currency fromString(String string) {
        return Currency.getInstance(string);
    }

    @Override
    public <X> X unwrap(Currency value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value.getCurrencyCode();
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Currency wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return Currency.getInstance((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

