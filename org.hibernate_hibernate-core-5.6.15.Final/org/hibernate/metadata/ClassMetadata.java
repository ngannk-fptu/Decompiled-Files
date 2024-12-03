/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metadata;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

public interface ClassMetadata {
    public String getEntityName();

    public String getIdentifierPropertyName();

    public String[] getPropertyNames();

    public Type getIdentifierType();

    public Type[] getPropertyTypes();

    public Type getPropertyType(String var1) throws HibernateException;

    public boolean hasProxy();

    public boolean isMutable();

    public boolean isVersioned();

    public int getVersionProperty();

    public boolean[] getPropertyNullability();

    public boolean[] getPropertyLaziness();

    public boolean hasIdentifierProperty();

    public boolean hasNaturalIdentifier();

    public int[] getNaturalIdentifierProperties();

    public boolean hasSubclasses();

    public boolean isInherited();

    @Deprecated
    default public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
        return this.getPropertyValuesToInsert(entity, mergeMap, (SharedSessionContractImplementor)session);
    }

    public Object[] getPropertyValuesToInsert(Object var1, Map var2, SharedSessionContractImplementor var3) throws HibernateException;

    public Class getMappedClass();

    @Deprecated
    default public Object instantiate(Serializable id, SessionImplementor session) {
        return this.instantiate(id, (SharedSessionContractImplementor)session);
    }

    public Object instantiate(Serializable var1, SharedSessionContractImplementor var2);

    public Object getPropertyValue(Object var1, String var2) throws HibernateException;

    public Object[] getPropertyValues(Object var1) throws HibernateException;

    public void setPropertyValue(Object var1, String var2, Object var3) throws HibernateException;

    public void setPropertyValues(Object var1, Object[] var2) throws HibernateException;

    @Deprecated
    public Serializable getIdentifier(Object var1) throws HibernateException;

    @Deprecated
    default public Serializable getIdentifier(Object entity, SessionImplementor session) {
        return this.getIdentifier(entity, (SharedSessionContractImplementor)session);
    }

    public Serializable getIdentifier(Object var1, SharedSessionContractImplementor var2);

    @Deprecated
    default public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
        this.setIdentifier(entity, id, (SharedSessionContractImplementor)session);
    }

    public void setIdentifier(Object var1, Serializable var2, SharedSessionContractImplementor var3);

    public boolean implementsLifecycle();

    public Object getVersion(Object var1) throws HibernateException;
}

