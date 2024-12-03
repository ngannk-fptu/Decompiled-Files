/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.HibernateException;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class ClassTypeDescriptor
extends AbstractTypeDescriptor<Class> {
    public static final ClassTypeDescriptor INSTANCE = new ClassTypeDescriptor();

    public ClassTypeDescriptor() {
        super(Class.class);
    }

    @Override
    public String toString(Class value) {
        return value.getName();
    }

    @Override
    public Class fromString(String string) {
        if (string == null) {
            return null;
        }
        try {
            return ReflectHelper.classForName(string);
        }
        catch (ClassNotFoundException e) {
            throw new HibernateException("Unable to locate named class " + string);
        }
    }

    @Override
    public <X> X unwrap(Class value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Class.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)this.toString(value);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Class wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Class.class.isInstance(value)) {
            return (Class)value;
        }
        if (String.class.isInstance(value)) {
            return this.fromString((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

