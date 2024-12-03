/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.secure.spi;

import java.util.Map;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.ServiceContributingIntegrator;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.secure.internal.DisabledJaccServiceImpl;
import org.hibernate.secure.internal.JaccPreDeleteEventListener;
import org.hibernate.secure.internal.JaccPreInsertEventListener;
import org.hibernate.secure.internal.JaccPreLoadEventListener;
import org.hibernate.secure.internal.JaccPreUpdateEventListener;
import org.hibernate.secure.internal.JaccSecurityListener;
import org.hibernate.secure.internal.StandardJaccServiceImpl;
import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.IntegrationException;
import org.hibernate.secure.spi.JaccPermissionDeclarations;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.jboss.logging.Logger;

@Deprecated
public class JaccIntegrator
implements ServiceContributingIntegrator {
    private static final Logger log = Logger.getLogger(JaccIntegrator.class);
    private static final DuplicationStrategy DUPLICATION_STRATEGY = new DuplicationStrategy(){

        @Override
        public boolean areMatch(Object listener, Object original) {
            return listener.getClass().equals(original.getClass()) && JaccSecurityListener.class.isInstance(original);
        }

        @Override
        public DuplicationStrategy.Action getAction() {
            return DuplicationStrategy.Action.KEEP_ORIGINAL;
        }
    };

    @Override
    public void prepareServices(StandardServiceRegistryBuilder serviceRegistryBuilder) {
        boolean isSecurityEnabled = serviceRegistryBuilder.getSettings().containsKey("hibernate.jacc.enabled");
        JaccService jaccService = isSecurityEnabled ? new StandardJaccServiceImpl() : new DisabledJaccServiceImpl();
        serviceRegistryBuilder.addService(JaccService.class, jaccService);
    }

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        this.doIntegration(serviceRegistry.getService(ConfigurationService.class).getSettings(), null, serviceRegistry);
    }

    private void doIntegration(Map properties, JaccPermissionDeclarations permissionDeclarations, SessionFactoryServiceRegistry serviceRegistry) {
        boolean isSecurityEnabled = properties.containsKey("hibernate.jacc.enabled");
        if (!isSecurityEnabled) {
            log.debug((Object)"Skipping JACC integration as it was not enabled");
            return;
        }
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedJaccUsage("hibernate.jacc.enabled", "hibernate.jacc_context_id", "hibernate.jacc");
        String contextId = (String)properties.get("hibernate.jacc_context_id");
        if (contextId == null) {
            throw new IntegrationException("JACC context id must be specified");
        }
        JaccService jaccService = serviceRegistry.getService(JaccService.class);
        if (jaccService == null) {
            throw new IntegrationException("JaccService was not set up");
        }
        if (permissionDeclarations != null) {
            for (GrantedPermission declaration : permissionDeclarations.getPermissionDeclarations()) {
                jaccService.addPermission(declaration);
            }
        }
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.addDuplicationStrategy(DUPLICATION_STRATEGY);
        eventListenerRegistry.prependListeners(EventType.PRE_DELETE, new JaccPreDeleteEventListener());
        eventListenerRegistry.prependListeners(EventType.PRE_INSERT, new JaccPreInsertEventListener());
        eventListenerRegistry.prependListeners(EventType.PRE_UPDATE, new JaccPreUpdateEventListener());
        eventListenerRegistry.prependListeners(EventType.PRE_LOAD, new JaccPreLoadEventListener());
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}

