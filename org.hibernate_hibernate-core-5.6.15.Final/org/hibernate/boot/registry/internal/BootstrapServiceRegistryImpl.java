/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.internal;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.internal.StrategySelectorImpl;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.integrator.internal.IntegratorServiceImpl;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Stoppable;

public class BootstrapServiceRegistryImpl
implements ServiceRegistryImplementor,
BootstrapServiceRegistry,
ServiceBinding.ServiceLifecycleOwner {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(BootstrapServiceRegistryImpl.class);
    private final boolean autoCloseRegistry;
    private boolean active = true;
    private static final LinkedHashSet<Integrator> NO_INTEGRATORS = new LinkedHashSet();
    private final ServiceBinding<ClassLoaderService> classLoaderServiceBinding;
    private final ServiceBinding<StrategySelector> strategySelectorBinding;
    private final ServiceBinding<IntegratorService> integratorServiceBinding;
    private Set<ServiceRegistryImplementor> childRegistries;

    public BootstrapServiceRegistryImpl() {
        this(new ClassLoaderServiceImpl(), NO_INTEGRATORS);
    }

    public BootstrapServiceRegistryImpl(ClassLoaderService classLoaderService, LinkedHashSet<Integrator> providedIntegrators) {
        this(true, classLoaderService, providedIntegrators);
    }

    public BootstrapServiceRegistryImpl(boolean autoCloseRegistry, ClassLoaderService classLoaderService, LinkedHashSet<Integrator> providedIntegrators) {
        this.autoCloseRegistry = autoCloseRegistry;
        this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(this, ClassLoaderService.class, classLoaderService);
        StrategySelectorImpl strategySelector = new StrategySelectorImpl(classLoaderService);
        this.strategySelectorBinding = new ServiceBinding<StrategySelectorImpl>(this, StrategySelector.class, strategySelector);
        this.integratorServiceBinding = new ServiceBinding<IntegratorServiceImpl>(this, IntegratorService.class, new IntegratorServiceImpl(providedIntegrators, classLoaderService));
    }

    public BootstrapServiceRegistryImpl(ClassLoaderService classLoaderService, StrategySelector strategySelector, IntegratorService integratorService) {
        this(true, classLoaderService, strategySelector, integratorService);
    }

    public BootstrapServiceRegistryImpl(boolean autoCloseRegistry, ClassLoaderService classLoaderService, StrategySelector strategySelector, IntegratorService integratorService) {
        this.autoCloseRegistry = autoCloseRegistry;
        this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(this, ClassLoaderService.class, classLoaderService);
        this.strategySelectorBinding = new ServiceBinding<StrategySelector>(this, StrategySelector.class, strategySelector);
        this.integratorServiceBinding = new ServiceBinding<IntegratorService>(this, IntegratorService.class, integratorService);
    }

    @Override
    public <R extends Service> R getService(Class<R> serviceRole) {
        ServiceBinding<R> binding = this.locateServiceBinding(serviceRole);
        return binding == null ? null : (R)binding.getService();
    }

    @Override
    public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
        if (ClassLoaderService.class.equals(serviceRole)) {
            return this.classLoaderServiceBinding;
        }
        if (StrategySelector.class.equals(serviceRole)) {
            return this.strategySelectorBinding;
        }
        if (IntegratorService.class.equals(serviceRole)) {
            return this.integratorServiceBinding;
        }
        return null;
    }

    @Override
    public synchronized void destroy() {
        if (!this.active) {
            return;
        }
        this.active = false;
        this.destroy(this.classLoaderServiceBinding);
        this.destroy(this.strategySelectorBinding);
        this.destroy(this.integratorServiceBinding);
        if (this.childRegistries != null) {
            for (ServiceRegistry serviceRegistry : this.childRegistries) {
                if (!(serviceRegistry instanceof ServiceRegistryImplementor)) continue;
                ServiceRegistryImplementor serviceRegistryImplementor = (ServiceRegistryImplementor)serviceRegistry;
                serviceRegistryImplementor.destroy();
            }
        }
    }

    private synchronized void destroy(ServiceBinding serviceBinding) {
        serviceBinding.getLifecycleOwner().stopService(serviceBinding);
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public ServiceRegistry getParentServiceRegistry() {
        return null;
    }

    @Override
    public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
        throw new ServiceException("Boot-strap registry should only contain provided services");
    }

    @Override
    public <R extends Service> void configureService(ServiceBinding<R> binding) {
        throw new ServiceException("Boot-strap registry should only contain provided services");
    }

    @Override
    public <R extends Service> void injectDependencies(ServiceBinding<R> binding) {
        throw new ServiceException("Boot-strap registry should only contain provided services");
    }

    @Override
    public <R extends Service> void startService(ServiceBinding<R> binding) {
        throw new ServiceException("Boot-strap registry should only contain provided services");
    }

    @Override
    public synchronized <R extends Service> void stopService(ServiceBinding<R> binding) {
        R service = binding.getService();
        if (Stoppable.class.isInstance(service)) {
            try {
                ((Stoppable)service).stop();
            }
            catch (Exception e) {
                LOG.unableToStopService(service.getClass(), e);
            }
        }
    }

    @Override
    public synchronized void registerChild(ServiceRegistryImplementor child) {
        if (this.childRegistries == null) {
            this.childRegistries = new HashSet<ServiceRegistryImplementor>();
        }
        if (!this.childRegistries.add(child)) {
            LOG.warnf("Child ServiceRegistry [%s] was already registered; this will end badly later...", child);
        }
    }

    @Override
    public synchronized void deRegisterChild(ServiceRegistryImplementor child) {
        if (this.childRegistries == null) {
            throw new IllegalStateException("No child ServiceRegistry registrations found");
        }
        this.childRegistries.remove(child);
        if (this.childRegistries.isEmpty()) {
            if (this.autoCloseRegistry) {
                LOG.debug("Implicitly destroying Boot-strap registry on de-registration of all child ServiceRegistries");
                this.destroy();
            } else {
                LOG.debug("Skipping implicitly destroying Boot-strap registry on de-registration of all child ServiceRegistries");
            }
        }
    }
}

