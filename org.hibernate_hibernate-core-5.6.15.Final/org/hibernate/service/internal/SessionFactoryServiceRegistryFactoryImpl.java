/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.internal;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.internal.SessionFactoryServiceRegistryBuilderImpl;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceContributor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;

public class SessionFactoryServiceRegistryFactoryImpl
implements SessionFactoryServiceRegistryFactory {
    private final ServiceRegistryImplementor theBasicServiceRegistry;

    public SessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
        this.theBasicServiceRegistry = theBasicServiceRegistry;
    }

    @Override
    public SessionFactoryServiceRegistry buildServiceRegistry(SessionFactoryImplementor sessionFactory, SessionFactoryOptions options) {
        ClassLoaderService cls = options.getServiceRegistry().getService(ClassLoaderService.class);
        SessionFactoryServiceRegistryBuilderImpl builder = new SessionFactoryServiceRegistryBuilderImpl(this.theBasicServiceRegistry);
        for (SessionFactoryServiceContributor contributor : cls.loadJavaServices(SessionFactoryServiceContributor.class)) {
            contributor.contribute(builder);
        }
        return builder.buildSessionFactoryServiceRegistry(sessionFactory, options);
    }
}

