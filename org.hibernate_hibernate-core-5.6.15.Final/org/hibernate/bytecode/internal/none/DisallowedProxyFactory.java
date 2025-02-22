/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal.none;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.type.CompositeType;

final class DisallowedProxyFactory
implements ProxyFactory {
    static final DisallowedProxyFactory INSTANCE = new DisallowedProxyFactory();

    DisallowedProxyFactory() {
    }

    @Override
    public void postInstantiate(String entityName, Class persistentClass, Set<Class> interfaces, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType) throws HibernateException {
    }

    @Override
    public HibernateProxy getProxy(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        throw new HibernateException("Generation of HibernateProxy instances at runtime is not allowed when the configured BytecodeProvider is 'none'; your model requires a more advanced BytecodeProvider to be enabled.");
    }
}

