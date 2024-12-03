/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.service.Service;
import org.hibernate.service.internal.AbstractServiceRegistryImpl;
import org.hibernate.service.internal.ProvidedService;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceInitiator;

public class StandardServiceRegistryImpl
extends AbstractServiceRegistryImpl
implements StandardServiceRegistry {
    private Map configurationValues;

    public StandardServiceRegistryImpl(BootstrapServiceRegistry bootstrapServiceRegistry, List<StandardServiceInitiator> serviceInitiators, List<ProvidedService> providedServices, Map<?, ?> configurationValues) {
        this(true, bootstrapServiceRegistry, serviceInitiators, providedServices, configurationValues);
    }

    public StandardServiceRegistryImpl(boolean autoCloseRegistry, BootstrapServiceRegistry bootstrapServiceRegistry, List<StandardServiceInitiator> serviceInitiators, List<ProvidedService> providedServices, Map<?, ?> configurationValues) {
        super(bootstrapServiceRegistry, autoCloseRegistry);
        this.configurationValues = configurationValues;
        this.applyServiceRegistrations(serviceInitiators, providedServices);
    }

    private void applyServiceRegistrations(List<StandardServiceInitiator> serviceInitiators, List<ProvidedService> providedServices) {
        try {
            for (ServiceInitiator serviceInitiator : serviceInitiators) {
                this.createServiceBinding(serviceInitiator);
            }
            for (ProvidedService providedService : providedServices) {
                this.createServiceBinding(providedService);
            }
        }
        catch (RuntimeException e) {
            this.visitServiceBindings(binding -> binding.getLifecycleOwner().stopService(binding));
            throw e;
        }
    }

    @Override
    public synchronized <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
        return ((StandardServiceInitiator)serviceInitiator).initiateService(this.configurationValues, this);
    }

    @Override
    public synchronized <R extends Service> void configureService(ServiceBinding<R> serviceBinding) {
        if (Configurable.class.isInstance(serviceBinding.getService())) {
            ((Configurable)serviceBinding.getService()).configure(this.configurationValues);
        }
    }

    public synchronized void resetAndReactivate(BootstrapServiceRegistry bootstrapServiceRegistry, List<StandardServiceInitiator> serviceInitiators, List<ProvidedService> providedServices, Map<?, ?> configurationValues) {
        if (super.isActive()) {
            throw new IllegalStateException("Can't reactivate an active registry!");
        }
        super.resetParent(bootstrapServiceRegistry);
        this.configurationValues = new HashMap(configurationValues);
        super.reactivate();
        this.applyServiceRegistrations(serviceInitiators, providedServices);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        this.configurationValues = null;
    }
}

