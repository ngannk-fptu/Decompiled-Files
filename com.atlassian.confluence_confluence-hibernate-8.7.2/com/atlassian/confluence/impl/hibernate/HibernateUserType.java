/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.usertype.UserType
 */
package com.atlassian.confluence.impl.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public abstract class HibernateUserType
implements UserType,
Serializable {
    public abstract Object nullSafeGetImpl(ResultSet var1, String[] var2, SharedSessionContractImplementor var3, Object var4) throws SQLException;

    public abstract void nullSafeSetImpl(PreparedStatement var1, Object var2, int var3, SharedSessionContractImplementor var4) throws SQLException;

    public final Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.nullSafeGetImpl(rs, names, session, owner);
    }

    public final void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value instanceof String && !this.returnedClass().isInstance(value)) {
            st.setString(index, (String)value);
            return;
        }
        this.nullSafeSetImpl(st, value, index, session);
    }

    public int hashCode(Object x) throws HibernateException {
        return x != null ? x.hashCode() : 0;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return value != null ? (Serializable)this.deepCopy(value) : null;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached != null ? this.deepCopy(cached) : null;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}

