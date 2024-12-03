/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.internal;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jmx.spi.JmxService;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.UnknownServiceException;
import org.hibernate.service.internal.ProvidedService;
import org.hibernate.service.internal.ServiceDependencyException;
import org.hibernate.service.spi.InjectService;
import org.hibernate.service.spi.Manageable;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Startable;
import org.hibernate.service.spi.Stoppable;

public abstract class AbstractServiceRegistryImpl
implements ServiceRegistryImplementor,
ServiceBinding.ServiceLifecycleOwner {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(AbstractServiceRegistryImpl.class);
    public static final String ALLOW_CRAWLING = "hibernate.service.allow_crawling";
    private volatile ServiceRegistryImplementor parent;
    private final boolean allowCrawling;
    private final ConcurrentMap<Class, ServiceBinding> serviceBindingMap = new ConcurrentHashMap<Class, ServiceBinding>();
    private final ConcurrentMap<Class, Class> roleXref = new ConcurrentHashMap<Class, Class>();
    private final ConcurrentMap<Class, Service> initializedServiceByRole = new ConcurrentHashMap<Class, Service>();
    private final List<ServiceBinding> serviceBindingList = CollectionHelper.arrayList(20);
    private boolean autoCloseRegistry;
    private Set<ServiceRegistryImplementor> childRegistries;
    private final AtomicBoolean active = new AtomicBoolean(true);

    protected AbstractServiceRegistryImpl() {
        this((ServiceRegistryImplementor)null);
    }

    protected AbstractServiceRegistryImpl(boolean autoCloseRegistry) {
        this((ServiceRegistryImplementor)null, autoCloseRegistry);
    }

    protected AbstractServiceRegistryImpl(ServiceRegistryImplementor parent) {
        this(parent, true);
    }

    protected AbstractServiceRegistryImpl(ServiceRegistryImplementor parent, boolean autoCloseRegistry) {
        this.parent = parent;
        this.allowCrawling = ConfigurationHelper.getBoolean(ALLOW_CRAWLING, Environment.getProperties(), true);
        this.autoCloseRegistry = autoCloseRegistry;
        this.parent.registerChild(this);
    }

    public AbstractServiceRegistryImpl(BootstrapServiceRegistry bootstrapServiceRegistry) {
        this(bootstrapServiceRegistry, true);
    }

    public AbstractServiceRegistryImpl(BootstrapServiceRegistry bootstrapServiceRegistry, boolean autoCloseRegistry) {
        if (!ServiceRegistryImplementor.class.isInstance(bootstrapServiceRegistry)) {
            throw new IllegalArgumentException("ServiceRegistry parent needs to implement ServiceRegistryImplementor");
        }
        this.parent = (ServiceRegistryImplementor)((Object)bootstrapServiceRegistry);
        this.allowCrawling = ConfigurationHelper.getBoolean(ALLOW_CRAWLING, Environment.getProperties(), true);
        this.autoCloseRegistry = autoCloseRegistry;
        this.parent.registerChild(this);
    }

    protected <R extends Service> void createServiceBinding(ServiceInitiator<R> initiator) {
        ServiceBinding<R> serviceBinding = new ServiceBinding<R>(this, initiator);
        this.serviceBindingMap.put(initiator.getServiceInitiated(), serviceBinding);
    }

    protected <R extends Service> void createServiceBinding(ProvidedService<R> providedService) {
        ServiceBinding<Object> binding = this.locateServiceBinding(providedService.getServiceRole(), false);
        if (binding == null) {
            binding = new ServiceBinding<Service>(this, providedService.getServiceRole(), (Service)providedService.getService());
            this.serviceBindingMap.put(providedService.getServiceRole(), binding);
        }
        this.registerService(binding, (Service)providedService.getService());
    }

    protected void visitServiceBindings(Consumer<ServiceBinding> action) {
        this.serviceBindingList.forEach(action);
    }

    @Override
    public ServiceRegistry getParentServiceRegistry() {
        return this.parent;
    }

    @Override
    public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
        return this.locateServiceBinding(serviceRole, true);
    }

    protected <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole, boolean checkParent) {
        ServiceBinding<R> serviceBinding = (ServiceBinding<R>)this.serviceBindingMap.get(serviceRole);
        if (serviceBinding == null && checkParent && this.parent != null) {
            serviceBinding = this.parent.locateServiceBinding(serviceRole);
        }
        if (serviceBinding != null) {
            return serviceBinding;
        }
        if (!this.allowCrawling) {
            return null;
        }
        Class alternative = (Class)this.roleXref.get(serviceRole);
        if (alternative != null) {
            return (ServiceBinding)this.serviceBindingMap.get(alternative);
        }
        for (ServiceBinding binding : this.serviceBindingMap.values()) {
            if (serviceRole.isAssignableFrom(binding.getServiceRole())) {
                log.alternateServiceRole(serviceRole.getName(), binding.getServiceRole().getName());
                this.registerAlternate(serviceRole, binding.getServiceRole());
                return binding;
            }
            if (binding.getService() == null || !serviceRole.isInstance(binding.getService())) continue;
            log.alternateServiceRole(serviceRole.getName(), binding.getServiceRole().getName());
            this.registerAlternate(serviceRole, binding.getServiceRole());
            return binding;
        }
        return null;
    }

    private void registerAlternate(Class alternate, Class target) {
        this.roleXref.put(alternate, target);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <R extends Service> R getService(Class<R> serviceRole) {
        if (ClassLoaderService.class.equals(serviceRole) && this.parent != null) {
            return this.parent.getService(serviceRole);
        }
        Service service = (Service)serviceRole.cast(this.initializedServiceByRole.get(serviceRole));
        if (service != null) {
            return (R)service;
        }
        AbstractServiceRegistryImpl abstractServiceRegistryImpl = this;
        synchronized (abstractServiceRegistryImpl) {
            service = (Service)serviceRole.cast(this.initializedServiceByRole.get(serviceRole));
            if (service != null) {
                return (R)service;
            }
            ServiceBinding<R> serviceBinding = this.locateServiceBinding(serviceRole);
            if (serviceBinding == null) {
                throw new UnknownServiceException(serviceRole);
            }
            service = serviceBinding.getService();
            if (service == null) {
                service = this.initializeService(serviceBinding);
            }
            if (service != null) {
                this.initializedServiceByRole.put(serviceRole, service);
            }
            return (R)service;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <R extends Service> void registerService(ServiceBinding<R> serviceBinding, R service) {
        serviceBinding.setService(service);
        List<ServiceBinding> list = this.serviceBindingList;
        synchronized (list) {
            this.serviceBindingList.add(serviceBinding);
        }
    }

    private <R extends Service> R initializeService(ServiceBinding<R> serviceBinding) {
        R service;
        if (log.isTraceEnabled()) {
            log.tracev("Initializing service [role={0}]", serviceBinding.getServiceRole().getName());
        }
        if ((service = this.createService(serviceBinding)) == null) {
            return null;
        }
        serviceBinding.getLifecycleOwner().injectDependencies(serviceBinding);
        serviceBinding.getLifecycleOwner().configureService(serviceBinding);
        serviceBinding.getLifecycleOwner().startService(serviceBinding);
        return service;
    }

    protected <R extends Service> R createService(ServiceBinding<R> serviceBinding) {
        ServiceInitiator<R> serviceInitiator = serviceBinding.getServiceInitiator();
        if (serviceInitiator == null) {
            throw new UnknownServiceException(serviceBinding.getServiceRole());
        }
        try {
            R service = serviceBinding.getLifecycleOwner().initiateService(serviceInitiator);
            if (service != null) {
                this.registerService(serviceBinding, service);
            }
            return service;
        }
        catch (ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException("Unable to create requested service [" + serviceBinding.getServiceRole().getName() + "]", e);
        }
    }

    @Override
    public <R extends Service> void injectDependencies(ServiceBinding<R> serviceBinding) {
        R service = serviceBinding.getService();
        this.applyInjections(service);
        if (ServiceRegistryAwareService.class.isInstance(service)) {
            ((ServiceRegistryAwareService)service).injectServices(this);
        }
    }

    private <R extends Service> void applyInjections(R service) {
        try {
            for (Method method : service.getClass().getMethods()) {
                InjectService injectService = method.getAnnotation(InjectService.class);
                if (injectService == null) continue;
                this.processInjection(service, method, injectService);
            }
        }
        catch (NullPointerException e) {
            log.error("NPE injecting service deps : " + service.getClass().getName());
        }
    }

    private <T extends Service> void processInjection(T service, Method injectionMethod, InjectService injectService) {
        Object dependantService;
        Class<?>[] parameterTypes = injectionMethod.getParameterTypes();
        if (parameterTypes == null || injectionMethod.getParameterCount() != 1) {
            throw new ServiceDependencyException("Encountered @InjectService on method with unexpected number of parameters");
        }
        Class<?> dependentServiceRole = injectService.serviceRole();
        if (dependentServiceRole == null || dependentServiceRole.equals(Void.class)) {
            dependentServiceRole = parameterTypes[0];
        }
        if ((dependantService = this.getService(dependentServiceRole)) == null) {
            if (injectService.required()) {
                throw new ServiceDependencyException("Dependency [" + dependentServiceRole + "] declared by service [" + service + "] not found");
            }
        } else {
            try {
                injectionMethod.invoke(service, dependantService);
            }
            catch (Exception e) {
                throw new ServiceDependencyException("Cannot inject dependency service", e);
            }
        }
    }

    @Override
    public <R extends Service> void startService(ServiceBinding<R> serviceBinding) {
        if (Startable.class.isInstance(serviceBinding.getService())) {
            ((Startable)serviceBinding.getService()).start();
        }
        if (Manageable.class.isInstance(serviceBinding.getService())) {
            this.getService(JmxService.class).registerService((Manageable)serviceBinding.getService(), serviceBinding.getServiceRole());
        }
    }

    public boolean isActive() {
        return this.active.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void destroy() {
        if (this.active.compareAndSet(true, false)) {
            try {
                this.initializedServiceByRole.clear();
                List<ServiceBinding> list = this.serviceBindingList;
                synchronized (list) {
                    ListIterator<ServiceBinding> serviceBindingsIterator = this.serviceBindingList.listIterator(this.serviceBindingList.size());
                    while (serviceBindingsIterator.hasPrevious()) {
                        ServiceBinding serviceBinding = serviceBindingsIterator.previous();
                        serviceBinding.getLifecycleOwner().stopService(serviceBinding);
                    }
                    this.serviceBindingList.clear();
                }
                this.serviceBindingMap.clear();
            }
            finally {
                this.parent.deRegisterChild(this);
            }
        }
    }

    @Override
    public synchronized <R extends Service> void stopService(ServiceBinding<R> binding) {
        R service = binding.getService();
        if (Stoppable.class.isInstance(service)) {
            try {
                ((Stoppable)service).stop();
            }
            catch (Exception e) {
                log.unableToStopService(service.getClass(), e);
            }
        }
    }

    @Override
    public synchronized void registerChild(ServiceRegistryImplementor child) {
        if (this.childRegistries == null) {
            this.childRegistries = new HashSet<ServiceRegistryImplementor>();
        }
        if (!this.childRegistries.add(child)) {
            log.warnf("Child ServiceRegistry [%s] was already registered; this will end badly later...", child);
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
                log.debug("Implicitly destroying ServiceRegistry on de-registration of all child ServiceRegistries");
                this.destroy();
            } else {
                log.debug("Skipping implicitly destroying ServiceRegistry on de-registration of all child ServiceRegistries");
            }
        }
    }

    public synchronized void resetParent(BootstrapServiceRegistry newParent) {
        if (this.parent != null) {
            this.parent.deRegisterChild(this);
        }
        if (newParent != null) {
            if (!ServiceRegistryImplementor.class.isInstance(newParent)) {
                throw new IllegalArgumentException("ServiceRegistry parent needs to implement ServiceRegistryImplementor");
            }
            this.parent = (ServiceRegistryImplementor)((Object)newParent);
            this.parent.registerChild(this);
        } else {
            this.parent = null;
        }
    }

    public synchronized void reactivate() {
        if (!this.active.compareAndSet(false, true)) {
            throw new IllegalStateException("Was not inactive, could not reactivate!");
        }
    }
}

