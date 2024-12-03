/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Closeable;
import java.io.Serializable;
import java.sql.Connection;
import org.hibernate.LockMode;
import org.hibernate.SharedSessionContract;
import org.hibernate.query.NativeQuery;

public interface StatelessSession
extends SharedSessionContract,
AutoCloseable,
Closeable {
    @Override
    public void close();

    public Serializable insert(Object var1);

    public Serializable insert(String var1, Object var2);

    public void update(Object var1);

    public void update(String var1, Object var2);

    public void delete(Object var1);

    public void delete(String var1, Object var2);

    public Object get(String var1, Serializable var2);

    public Object get(Class var1, Serializable var2);

    public Object get(String var1, Serializable var2, LockMode var3);

    public Object get(Class var1, Serializable var2, LockMode var3);

    public void refresh(Object var1);

    public void refresh(String var1, Object var2);

    public void refresh(Object var1, LockMode var2);

    public void refresh(String var1, Object var2, LockMode var3);

    @Deprecated
    public Connection connection();

    @Override
    public NativeQuery createSQLQuery(String var1);
}

