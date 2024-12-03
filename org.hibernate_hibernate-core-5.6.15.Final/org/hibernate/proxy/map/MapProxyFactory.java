/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.map;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.proxy.map.MapLazyInitializer;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.type.CompositeType;

public class MapProxyFactory
implements ProxyFactory {
    private String entityName;

    public void postInstantiate(String entityName, Class persistentClass, Set interfaces, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType) throws HibernateException {
        this.entityName = entityName;
    }

    @Override
    public HibernateProxy getProxy(Serializable id, SharedSessionContractImplementor session) {
        return new MapProxy(new MapLazyInitializer(this.entityName, id, session));
    }
}

