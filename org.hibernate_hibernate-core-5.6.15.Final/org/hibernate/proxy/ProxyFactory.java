/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.CompositeType;

public interface ProxyFactory {
    public void postInstantiate(String var1, Class var2, Set<Class> var3, Method var4, Method var5, CompositeType var6) throws HibernateException;

    public HibernateProxy getProxy(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;
}

