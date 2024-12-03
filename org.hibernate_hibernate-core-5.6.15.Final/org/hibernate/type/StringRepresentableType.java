/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.HibernateException;

public interface StringRepresentableType<T> {
    public String toString(T var1) throws HibernateException;

    public T fromStringValue(String var1) throws HibernateException;
}

