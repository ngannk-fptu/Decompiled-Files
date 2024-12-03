/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

import org.hibernate.bytecode.spi.BasicProxyFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.service.Service;

public interface ProxyFactoryFactory
extends Service {
    public ProxyFactory buildProxyFactory(SessionFactoryImplementor var1);

    @Deprecated
    public BasicProxyFactory buildBasicProxyFactory(Class var1, Class[] var2);

    public BasicProxyFactory buildBasicProxyFactory(Class var1);
}

