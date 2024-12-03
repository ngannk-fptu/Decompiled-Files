/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.net.MalformedURLException;
import java.net.URL;
import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class UrlTypeDescriptor
extends AbstractTypeDescriptor<URL> {
    public static final UrlTypeDescriptor INSTANCE = new UrlTypeDescriptor();

    public UrlTypeDescriptor() {
        super(URL.class);
    }

    @Override
    public String toString(URL value) {
        return value.toExternalForm();
    }

    @Override
    public URL fromString(String string) {
        try {
            return new URL(string);
        }
        catch (MalformedURLException e) {
            throw new HibernateException("Unable to convert string [" + string + "] to URL : " + e);
        }
    }

    @Override
    public <X> X unwrap(URL value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)this.toString(value);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> URL wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return this.fromString((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

