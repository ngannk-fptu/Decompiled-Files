/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface LazyInitializer {
    public void initialize() throws HibernateException;

    default public Serializable getInternalIdentifier() {
        return this.getIdentifier();
    }

    public Serializable getIdentifier();

    public void setIdentifier(Serializable var1);

    public String getEntityName();

    public Class getPersistentClass();

    public boolean isUninitialized();

    public Object getImplementation();

    public Object getImplementation(SharedSessionContractImplementor var1) throws HibernateException;

    public void setImplementation(Object var1);

    public boolean isReadOnlySettingAvailable();

    public boolean isReadOnly();

    public void setReadOnly(boolean var1);

    public SharedSessionContractImplementor getSession();

    public void setSession(SharedSessionContractImplementor var1) throws HibernateException;

    public void unsetSession();

    public void setUnwrap(boolean var1);

    public boolean isUnwrap();
}

