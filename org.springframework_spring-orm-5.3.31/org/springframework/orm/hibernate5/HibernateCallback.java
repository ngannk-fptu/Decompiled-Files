/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.hibernate5;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface HibernateCallback<T> {
    @Nullable
    public T doInHibernate(Session var1) throws HibernateException;
}

