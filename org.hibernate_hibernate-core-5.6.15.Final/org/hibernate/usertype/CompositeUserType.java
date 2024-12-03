/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

public interface CompositeUserType {
    public String[] getPropertyNames();

    public Type[] getPropertyTypes();

    public Object getPropertyValue(Object var1, int var2) throws HibernateException;

    public void setPropertyValue(Object var1, int var2, Object var3) throws HibernateException;

    public Class returnedClass();

    public boolean equals(Object var1, Object var2) throws HibernateException;

    public int hashCode(Object var1) throws HibernateException;

    public Object nullSafeGet(ResultSet var1, String[] var2, SharedSessionContractImplementor var3, Object var4) throws HibernateException, SQLException;

    public void nullSafeSet(PreparedStatement var1, Object var2, int var3, SharedSessionContractImplementor var4) throws HibernateException, SQLException;

    public Object deepCopy(Object var1) throws HibernateException;

    public boolean isMutable();

    public Serializable disassemble(Object var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Object assemble(Serializable var1, SharedSessionContractImplementor var2, Object var3) throws HibernateException;

    public Object replace(Object var1, Object var2, SharedSessionContractImplementor var3, Object var4) throws HibernateException;
}

