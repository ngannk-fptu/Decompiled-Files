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

public interface UserType {
    public int[] sqlTypes();

    public Class returnedClass();

    public boolean equals(Object var1, Object var2) throws HibernateException;

    public int hashCode(Object var1) throws HibernateException;

    public Object nullSafeGet(ResultSet var1, String[] var2, SharedSessionContractImplementor var3, Object var4) throws HibernateException, SQLException;

    public void nullSafeSet(PreparedStatement var1, Object var2, int var3, SharedSessionContractImplementor var4) throws HibernateException, SQLException;

    public Object deepCopy(Object var1) throws HibernateException;

    public boolean isMutable();

    public Serializable disassemble(Object var1) throws HibernateException;

    public Object assemble(Serializable var1, Object var2) throws HibernateException;

    public Object replace(Object var1, Object var2, Object var3) throws HibernateException;
}

