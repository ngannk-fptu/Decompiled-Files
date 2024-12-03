/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public final class ProxyFactoryFactoryInitiator
implements StandardServiceInitiator<ProxyFactoryFactory> {
    public static final StandardServiceInitiator<ProxyFactoryFactory> INSTANCE = new ProxyFactoryFactoryInitiator();

    @Override
    public ProxyFactoryFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        BytecodeProvider bytecodeProvider = registry.getService(BytecodeProvider.class);
        return bytecodeProvider.getProxyFactoryFactory();
    }

    @Override
    public Class<ProxyFactoryFactory> getServiceInitiated() {
        return ProxyFactoryFactory.class;
    }
}

