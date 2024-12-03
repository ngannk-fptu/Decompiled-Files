/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.tuple.Tuplizer;

public interface EntityTuplizer
extends Tuplizer {
    public EntityMode getEntityMode();

    @Deprecated
    public Object instantiate(Serializable var1) throws HibernateException;

    public Object instantiate(Serializable var1, SharedSessionContractImplementor var2);

    @Deprecated
    public Serializable getIdentifier(Object var1) throws HibernateException;

    public Serializable getIdentifier(Object var1, SharedSessionContractImplementor var2);

    @Deprecated
    public void setIdentifier(Object var1, Serializable var2) throws HibernateException;

    public void setIdentifier(Object var1, Serializable var2, SharedSessionContractImplementor var3);

    @Deprecated
    public void resetIdentifier(Object var1, Serializable var2, Object var3);

    public void resetIdentifier(Object var1, Serializable var2, Object var3, SharedSessionContractImplementor var4);

    public Object getVersion(Object var1) throws HibernateException;

    public void setPropertyValue(Object var1, int var2, Object var3) throws HibernateException;

    public void setPropertyValue(Object var1, String var2, Object var3) throws HibernateException;

    public Object[] getPropertyValuesToInsert(Object var1, Map var2, SharedSessionContractImplementor var3) throws HibernateException;

    public Object getPropertyValue(Object var1, String var2) throws HibernateException;

    public void afterInitialize(Object var1, SharedSessionContractImplementor var2);

    public boolean hasProxy();

    public Object createProxy(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;

    public boolean isLifecycleImplementor();

    public Class getConcreteProxyClass();

    public EntityNameResolver[] getEntityNameResolvers();

    public String determineConcreteSubclassEntityName(Object var1, SessionFactoryImplementor var2);

    public Getter getIdentifierGetter();

    public Getter getVersionGetter();

    default public ProxyFactory getProxyFactory() {
        return null;
    }
}

