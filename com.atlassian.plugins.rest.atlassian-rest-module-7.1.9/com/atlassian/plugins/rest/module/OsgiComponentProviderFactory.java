/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.module.OsgiResourceConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.server.impl.container.servlet.JSPTemplateProcessor;
import com.sun.jersey.spi.resource.PerRequest;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class OsgiComponentProviderFactory
implements IoCComponentProviderFactory {
    private static final Set<Class<?>> EXCLUDE = Collections.singleton(JSPTemplateProcessor.class);
    private final ContainerManagedPlugin plugin;
    private final Set<Class<?>> classes;
    private final Set<?> instances;

    public OsgiComponentProviderFactory(ResourceConfig resourceConfig, ContainerManagedPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        Set<Class<?>> providerClasses = Objects.requireNonNull(resourceConfig).getClasses();
        this.classes = providerClasses != null ? Sets.difference((Set)ImmutableSet.copyOf(providerClasses), EXCLUDE) : Collections.emptySet();
        this.instances = resourceConfig instanceof OsgiResourceConfig ? ((OsgiResourceConfig)resourceConfig).getInstances() : Collections.emptySet();
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c) {
        return this.getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> c) {
        if (!this.classes.contains(c)) {
            return null;
        }
        Object instance = this.getInstance(c);
        return instance == null ? new ContainerManagedComponentProvider(this.plugin, c) : new InstanceOsgiComponentProvider(instance);
    }

    private Object getInstance(Class<?> c) {
        for (Object o : this.instances) {
            if (!o.getClass().equals(c)) continue;
            return o;
        }
        return null;
    }

    private static class InstanceOsgiComponentProvider
    implements IoCManagedComponentProvider {
        private final Object instance;

        public InstanceOsgiComponentProvider(Object instance) {
            this.instance = Objects.requireNonNull(instance);
        }

        @Override
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        @Override
        public Object getInstance() {
            return this.instance;
        }

        @Override
        public Object getInjectableInstance(Object o) {
            return o;
        }
    }

    private static class ContainerManagedComponentProvider
    implements IoCManagedComponentProvider {
        private final ContainerManagedPlugin plugin;
        private final Class<?> componentClass;

        public ContainerManagedComponentProvider(ContainerManagedPlugin plugin, Class<?> componentClass) {
            this.plugin = plugin;
            this.componentClass = componentClass;
        }

        @Override
        public Object getInstance() {
            return this.plugin.getContainerAccessor().createBean(this.componentClass);
        }

        @Override
        public ComponentScope getScope() {
            if (this.componentClass.getAnnotation(PerRequest.class) != null) {
                return ComponentScope.PerRequest;
            }
            return ComponentScope.Singleton;
        }

        @Override
        public Object getInjectableInstance(Object o) {
            this.plugin.getContainerAccessor().injectBean(o);
            return o;
        }
    }
}

