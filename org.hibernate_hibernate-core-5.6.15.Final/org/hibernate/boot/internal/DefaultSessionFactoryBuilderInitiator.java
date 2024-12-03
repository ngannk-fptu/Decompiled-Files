/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.internal;

import java.util.Map;
import org.hibernate.boot.internal.DefaultSessionFactoryBuilderService;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.spi.SessionFactoryBuilderService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public final class DefaultSessionFactoryBuilderInitiator
implements StandardServiceInitiator<SessionFactoryBuilderService> {
    public static final DefaultSessionFactoryBuilderInitiator INSTANCE = new DefaultSessionFactoryBuilderInitiator();

    private DefaultSessionFactoryBuilderInitiator() {
    }

    @Override
    public SessionFactoryBuilderService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return DefaultSessionFactoryBuilderService.INSTANCE;
    }

    @Override
    public Class<SessionFactoryBuilderService> getServiceInitiated() {
        return SessionFactoryBuilderService.class;
    }
}

