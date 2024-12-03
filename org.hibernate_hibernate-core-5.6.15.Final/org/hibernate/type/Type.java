/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.ForeignKeyDirection;

public interface Type
extends Serializable {
    public boolean isAssociationType();

    public boolean isCollectionType();

    public boolean isEntityType();

    public boolean isAnyType();

    public boolean isComponentType();

    public int getColumnSpan(Mapping var1) throws MappingException;

    public int[] sqlTypes(Mapping var1) throws MappingException;

    public Size[] dictatedSizes(Mapping var1) throws MappingException;

    public Size[] defaultSizes(Mapping var1) throws MappingException;

    public Class getReturnedClass();

    public boolean isSame(Object var1, Object var2) throws HibernateException;

    public boolean isEqual(Object var1, Object var2) throws HibernateException;

    public boolean isEqual(Object var1, Object var2, SessionFactoryImplementor var3) throws HibernateException;

    public int getHashCode(Object var1) throws HibernateException;

    public int getHashCode(Object var1, SessionFactoryImplementor var2) throws HibernateException;

    public int compare(Object var1, Object var2);

    public boolean isDirty(Object var1, Object var2, SharedSessionContractImplementor var3) throws HibernateException;

    public boolean isDirty(Object var1, Object var2, boolean[] var3, SharedSessionContractImplementor var4) throws HibernateException;

    public boolean isModified(Object var1, Object var2, boolean[] var3, SharedSessionContractImplementor var4) throws HibernateException;

    public Object nullSafeGet(ResultSet var1, String[] var2, SharedSessionContractImplementor var3, Object var4) throws HibernateException, SQLException;

    public Object nullSafeGet(ResultSet var1, String var2, SharedSessionContractImplementor var3, Object var4) throws HibernateException, SQLException;

    public void nullSafeSet(PreparedStatement var1, Object var2, int var3, boolean[] var4, SharedSessionContractImplementor var5) throws HibernateException, SQLException;

    public void nullSafeSet(PreparedStatement var1, Object var2, int var3, SharedSessionContractImplementor var4) throws HibernateException, SQLException;

    public String toLoggableString(Object var1, SessionFactoryImplementor var2) throws HibernateException;

    public String getName();

    public Object deepCopy(Object var1, SessionFactoryImplementor var2) throws HibernateException;

    public boolean isMutable();

    public Serializable disassemble(Object var1, SharedSessionContractImplementor var2, Object var3) throws HibernateException;

    public Object assemble(Serializable var1, SharedSessionContractImplementor var2, Object var3) throws HibernateException;

    public void beforeAssemble(Serializable var1, SharedSessionContractImplementor var2);

    public Object hydrate(ResultSet var1, String[] var2, SharedSessionContractImplementor var3, Object var4) throws HibernateException, SQLException;

    public Object resolve(Object var1, SharedSessionContractImplementor var2, Object var3) throws HibernateException;

    default public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        return this.resolve(value, session, owner);
    }

    public Object semiResolve(Object var1, SharedSessionContractImplementor var2, Object var3) throws HibernateException;

    public Type getSemiResolvedType(SessionFactoryImplementor var1);

    public Object replace(Object var1, Object var2, SharedSessionContractImplementor var3, Object var4, Map var5) throws HibernateException;

    public Object replace(Object var1, Object var2, SharedSessionContractImplementor var3, Object var4, Map var5, ForeignKeyDirection var6) throws HibernateException;

    public boolean[] toColumnNullness(Object var1, Mapping var2);
}

