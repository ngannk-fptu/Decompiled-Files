/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal.none;

import org.hibernate.bytecode.internal.none.DisallowedProxyFactory;
import org.hibernate.bytecode.internal.none.NoneBasicProxyFactory;
import org.hibernate.bytecode.spi.BasicProxyFactory;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.proxy.ProxyFactory;

final class NoProxyFactoryFactory
implements ProxyFactoryFactory {
    NoProxyFactoryFactory() {
    }

    @Override
    public ProxyFactory buildProxyFactory(SessionFactoryImplementor sessionFactory) {
        return DisallowedProxyFactory.INSTANCE;
    }

    @Override
    public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces) {
        return new NoneBasicProxyFactory(superClass, interfaces);
    }

    @Override
    public BasicProxyFactory buildBasicProxyFactory(Class superClassOrInterface) {
        return new NoneBasicProxyFactory(superClassOrInterface);
    }
}

