/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.event.internal.EntityCopyAllowedLoggedObserver;
import org.hibernate.event.internal.EntityCopyAllowedObserver;
import org.hibernate.event.internal.EntityCopyNotAllowedObserver;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EntityCopyObserverFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class EntityCopyObserverFactoryInitiator
implements StandardServiceInitiator<EntityCopyObserverFactory> {
    public static final EntityCopyObserverFactoryInitiator INSTANCE = new EntityCopyObserverFactoryInitiator();
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EntityCopyObserverFactoryInitiator.class);

    @Override
    public EntityCopyObserverFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object value = this.getConfigurationValue(configurationValues);
        if (value.equals("disallow") || value.equals(EntityCopyNotAllowedObserver.class.getName())) {
            LOG.debugf("Configured EntityCopyObserver strategy: %s", "disallow");
            return EntityCopyNotAllowedObserver.FACTORY_OF_SELF;
        }
        if (value.equals("allow") || value.equals(EntityCopyAllowedObserver.class.getName())) {
            LOG.debugf("Configured EntityCopyObserver strategy: %s", "allow");
            return EntityCopyAllowedObserver.FACTORY_OF_SELF;
        }
        if (value.equals("log") || value.equals(EntityCopyAllowedLoggedObserver.class.getName())) {
            LOG.debugf("Configured EntityCopyObserver strategy: %s", "log");
            return EntityCopyAllowedLoggedObserver.FACTORY_OF_SELF;
        }
        EntityCopyObserver exampleInstance = registry.getService(StrategySelector.class).resolveStrategy(EntityCopyObserver.class, value);
        Class<?> observerType = exampleInstance.getClass();
        LOG.debugf("Configured EntityCopyObserver is a custom implementation of type %s", observerType.getName());
        return new EntityObserversFactoryFromClass(observerType);
    }

    private Object getConfigurationValue(Map configurationValues) {
        Object o = configurationValues.get("hibernate.event.merge.entity_copy_observer");
        if (o == null) {
            return "disallow";
        }
        if (o instanceof String) {
            return o.toString().trim();
        }
        return o;
    }

    @Override
    public Class<EntityCopyObserverFactory> getServiceInitiated() {
        return EntityCopyObserverFactory.class;
    }

    private static class EntityObserversFactoryFromClass
    implements EntityCopyObserverFactory {
        private final Class value;

        public EntityObserversFactoryFromClass(Class value) {
            this.value = value;
        }

        @Override
        public EntityCopyObserver createEntityCopyObserver() {
            try {
                return (EntityCopyObserver)this.value.newInstance();
            }
            catch (Exception e) {
                throw new HibernateException("Could not instantiate class of type " + this.value.getName());
            }
        }
    }
}

