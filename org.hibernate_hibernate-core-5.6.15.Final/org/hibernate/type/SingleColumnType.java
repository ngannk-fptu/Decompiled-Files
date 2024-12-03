/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

public interface SingleColumnType<T>
extends Type {
    public int sqlType();

    public String toString(T var1) throws HibernateException;

    public T fromStringValue(String var1) throws HibernateException;

    public T nullSafeGet(ResultSet var1, String var2, SharedSessionContractImplementor var3) throws HibernateException, SQLException;

    public Object get(ResultSet var1, String var2, SharedSessionContractImplementor var3) throws HibernateException, SQLException;

    public void set(PreparedStatement var1, T var2, int var3, SharedSessionContractImplementor var4) throws HibernateException, SQLException;
}

