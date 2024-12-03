/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.boot.registry.selector.internal.StrategySelectorBuilder;
import org.hibernate.integrator.internal.IntegratorServiceImpl;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.ServiceRegistry;

public class BootstrapServiceRegistryBuilder {
    private final LinkedHashSet<Integrator> providedIntegrators = new LinkedHashSet();
    private List<ClassLoader> providedClassLoaders;
    private ClassLoaderService providedClassLoaderService;
    private StrategySelectorBuilder strategySelectorBuilder = new StrategySelectorBuilder();
    private TcclLookupPrecedence tcclLookupPrecedence = TcclLookupPrecedence.AFTER;
    private boolean autoCloseRegistry = true;

    @Deprecated
    public BootstrapServiceRegistryBuilder with(Integrator integrator) {
        return this.applyIntegrator(integrator);
    }

    public BootstrapServiceRegistryBuilder applyIntegrator(Integrator integrator) {
        this.providedIntegrators.add(integrator);
        return this;
    }

    @Deprecated
    public BootstrapServiceRegistryBuilder with(ClassLoader classLoader) {
        return this.applyClassLoader(classLoader);
    }

    public BootstrapServiceRegistryBuilder applyClassLoader(ClassLoader classLoader) {
        if (this.providedClassLoaders == null) {
            this.providedClassLoaders = new ArrayList<ClassLoader>();
        }
        this.providedClassLoaders.add(classLoader);
        return this;
    }

    public void applyTcclLookupPrecedence(TcclLookupPrecedence precedence) {
        this.tcclLookupPrecedence = precedence;
    }

    @Deprecated
    public BootstrapServiceRegistryBuilder with(ClassLoaderService classLoaderService) {
        return this.applyClassLoaderService(classLoaderService);
    }

    public BootstrapServiceRegistryBuilder applyClassLoaderService(ClassLoaderService classLoaderService) {
        this.providedClassLoaderService = classLoaderService;
        return this;
    }

    @Deprecated
    public <T> BootstrapServiceRegistryBuilder withStrategySelector(Class<T> strategy, String name, Class<? extends T> implementation) {
        return this.applyStrategySelector(strategy, name, implementation);
    }

    public <T> BootstrapServiceRegistryBuilder applyStrategySelector(Class<T> strategy, String name, Class<? extends T> implementation) {
        this.strategySelectorBuilder.addExplicitStrategyRegistration(strategy, implementation, name);
        return this;
    }

    @Deprecated
    public BootstrapServiceRegistryBuilder withStrategySelectors(StrategyRegistrationProvider strategyRegistrationProvider) {
        return this.applyStrategySelectors(strategyRegistrationProvider);
    }

    public BootstrapServiceRegistryBuilder applyStrategySelectors(StrategyRegistrationProvider strategyRegistrationProvider) {
        for (StrategyRegistration strategyRegistration : strategyRegistrationProvider.getStrategyRegistrations()) {
            this.strategySelectorBuilder.addExplicitStrategyRegistration(strategyRegistration);
        }
        return this;
    }

    public BootstrapServiceRegistryBuilder disableAutoClose() {
        this.autoCloseRegistry = false;
        return this;
    }

    public BootstrapServiceRegistryBuilder enableAutoClose() {
        this.autoCloseRegistry = true;
        return this;
    }

    public BootstrapServiceRegistry build() {
        ClassLoaderService classLoaderService;
        if (this.providedClassLoaderService == null) {
            HashSet<ClassLoader> classLoaders = new HashSet<ClassLoader>();
            if (this.providedClassLoaders != null) {
                classLoaders.addAll(this.providedClassLoaders);
            }
            classLoaderService = new ClassLoaderServiceImpl(classLoaders, this.tcclLookupPrecedence);
        } else {
            classLoaderService = this.providedClassLoaderService;
        }
        IntegratorServiceImpl integratorService = new IntegratorServiceImpl(this.providedIntegrators, classLoaderService);
        return new BootstrapServiceRegistryImpl(this.autoCloseRegistry, classLoaderService, this.strategySelectorBuilder.buildSelector(classLoaderService), integratorService);
    }

    public static void destroy(ServiceRegistry serviceRegistry) {
        if (serviceRegistry == null) {
            return;
        }
        ((BootstrapServiceRegistryImpl)serviceRegistry).destroy();
    }
}

