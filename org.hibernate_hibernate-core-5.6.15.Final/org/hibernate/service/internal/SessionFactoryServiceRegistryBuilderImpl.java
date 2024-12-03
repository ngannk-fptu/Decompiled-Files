/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.internal;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.Service;
import org.hibernate.service.internal.ProvidedService;
import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
import org.hibernate.service.internal.StandardSessionFactoryServiceInitiators;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistryBuilder;

public class SessionFactoryServiceRegistryBuilderImpl
implements SessionFactoryServiceRegistryBuilder {
    private final ServiceRegistryImplementor parent;
    private final List<SessionFactoryServiceInitiator> initiators = StandardSessionFactoryServiceInitiators.buildStandardServiceInitiatorList();
    private final List<ProvidedService> providedServices = new ArrayList<ProvidedService>();

    public SessionFactoryServiceRegistryBuilderImpl(ServiceRegistryImplementor parent) {
        this.parent = parent;
    }

    @Override
    public SessionFactoryServiceRegistryBuilder addInitiator(SessionFactoryServiceInitiator initiator) {
        this.initiators.add(initiator);
        return this;
    }

    @Override
    public SessionFactoryServiceRegistryBuilder addService(Class serviceRole, Service service) {
        this.providedServices.add(new ProvidedService<Service>(serviceRole, service));
        return this;
    }

    public SessionFactoryServiceRegistry buildSessionFactoryServiceRegistry(SessionFactoryImplementor sessionFactory, SessionFactoryOptions options) {
        return new SessionFactoryServiceRegistryImpl(this.parent, this.initiators, this.providedServices, sessionFactory, options);
    }
}

