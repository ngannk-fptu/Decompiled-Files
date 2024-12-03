/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryImpl;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;

public class SessionFactoryServiceRegistryFactoryInitiator
implements StandardServiceInitiator<SessionFactoryServiceRegistryFactory> {
    public static final SessionFactoryServiceRegistryFactoryInitiator INSTANCE = new SessionFactoryServiceRegistryFactoryInitiator();

    @Override
    public Class<SessionFactoryServiceRegistryFactory> getServiceInitiated() {
        return SessionFactoryServiceRegistryFactory.class;
    }

    @Override
    public SessionFactoryServiceRegistryFactoryImpl initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new SessionFactoryServiceRegistryFactoryImpl(registry);
    }
}

