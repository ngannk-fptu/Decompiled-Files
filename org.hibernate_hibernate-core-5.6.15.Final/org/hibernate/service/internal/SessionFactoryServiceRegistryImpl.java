/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.service.internal;

import java.util.List;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.service.Service;
import org.hibernate.service.internal.AbstractServiceRegistryImpl;
import org.hibernate.service.internal.ProvidedService;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;
import org.hibernate.service.spi.SessionFactoryServiceInitiatorContext;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.jboss.logging.Logger;

public class SessionFactoryServiceRegistryImpl
extends AbstractServiceRegistryImpl
implements SessionFactoryServiceRegistry,
SessionFactoryServiceInitiatorContext {
    private static final Logger log = Logger.getLogger(SessionFactoryServiceRegistryImpl.class);
    private final SessionFactoryOptions sessionFactoryOptions;
    private final SessionFactoryImplementor sessionFactory;

    public SessionFactoryServiceRegistryImpl(ServiceRegistryImplementor parent, List<SessionFactoryServiceInitiator> initiators, List<ProvidedService> providedServices, SessionFactoryImplementor sessionFactory, SessionFactoryOptions sessionFactoryOptions) {
        super(parent);
        this.sessionFactory = sessionFactory;
        this.sessionFactoryOptions = sessionFactoryOptions;
        for (SessionFactoryServiceInitiator initiator : initiators) {
            this.createServiceBinding(initiator);
        }
        for (ProvidedService providedService : providedServices) {
            this.createServiceBinding(providedService);
        }
    }

    @Override
    public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
        SessionFactoryServiceInitiator sessionFactoryServiceInitiator = (SessionFactoryServiceInitiator)serviceInitiator;
        return sessionFactoryServiceInitiator.initiateService(this);
    }

    @Override
    public <R extends Service> void configureService(ServiceBinding<R> serviceBinding) {
        if (serviceBinding.getService() instanceof Configurable) {
            ((Configurable)serviceBinding.getService()).configure(this.getService(ConfigurationService.class).getSettings());
        }
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return this.sessionFactoryOptions;
    }

    @Override
    public ServiceRegistryImplementor getServiceRegistry() {
        return this;
    }

    @Override
    public <R extends Service> R getService(Class<R> serviceRole) {
        if (serviceRole.equals(EventListenerRegistry.class)) {
            log.debug((Object)"EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead");
            return (R)this.sessionFactory.getEventEngine().getListenerRegistry();
        }
        return super.getService(serviceRole);
    }
}

