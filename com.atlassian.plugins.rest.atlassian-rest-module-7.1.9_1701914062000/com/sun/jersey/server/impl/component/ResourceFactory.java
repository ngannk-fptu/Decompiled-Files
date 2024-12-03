/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.component;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ComponentConstructor;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.impl.resource.PerRequestFactory;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactoryClass;
import com.sun.jersey.spi.inject.Errors;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Map;

public class ResourceFactory {
    private final ResourceConfig config;
    private final ServerInjectableProviderContext ipc;
    private final Map<Class, ResourceComponentProviderFactory> factories;

    public ResourceFactory(ResourceConfig config, ServerInjectableProviderContext ipc) {
        this.config = config;
        this.ipc = ipc;
        this.factories = new HashMap<Class, ResourceComponentProviderFactory>();
    }

    public ServerInjectableProviderContext getInjectableProviderContext() {
        return this.ipc;
    }

    public ComponentScope getScope(Class c) {
        return this.getComponentProviderFactory(c).getScope(c);
    }

    public ResourceComponentProvider getComponentProvider(ComponentContext cc, Class c) {
        return (ResourceComponentProvider)this.getComponentProviderFactory(c).getComponentProvider(c);
    }

    protected ResourceComponentProviderFactory getComponentProviderFactory(Class c) {
        ResourceComponentProviderFactory rcpf;
        Class providerFactoryClass = null;
        Class<? extends Annotation> scope = null;
        for (Annotation a : c.getAnnotations()) {
            Class<? extends Annotation> annotationType = a.annotationType();
            ResourceComponentProviderFactoryClass rf = annotationType.getAnnotation(ResourceComponentProviderFactoryClass.class);
            if (rf != null && providerFactoryClass == null) {
                providerFactoryClass = rf.value();
                scope = annotationType;
                continue;
            }
            if (rf == null || providerFactoryClass == null) continue;
            Errors.error("Class " + c.getName() + " is annotated with multiple scopes: " + scope.getName() + " and " + annotationType.getName());
        }
        if (providerFactoryClass == null) {
            Object v = this.config.getProperties().get("com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass");
            if (v == null) {
                providerFactoryClass = PerRequestFactory.class;
            } else if (v instanceof String) {
                try {
                    providerFactoryClass = this.getSubclass(AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA((String)v)));
                }
                catch (ClassNotFoundException ex) {
                    throw new ContainerException(ex);
                }
                catch (PrivilegedActionException pae) {
                    throw new ContainerException(pae.getCause());
                }
            } else if (v instanceof Class) {
                providerFactoryClass = this.getSubclass((Class)v);
            } else {
                throw new IllegalArgumentException("Property value for com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass of type Class or String");
            }
        }
        if ((rcpf = this.factories.get(providerFactoryClass)) == null) {
            rcpf = this.getInstance(providerFactoryClass);
            this.factories.put(providerFactoryClass, rcpf);
        }
        return rcpf;
    }

    private Class<? extends ResourceComponentProviderFactory> getSubclass(Class<?> c) {
        if (ResourceComponentProviderFactory.class.isAssignableFrom(c)) {
            return c.asSubclass(ResourceComponentProviderFactory.class);
        }
        throw new IllegalArgumentException("Property value for com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass of type " + c + " not of a subclass of " + ResourceComponentProviderFactory.class);
    }

    private ResourceComponentProviderFactory getInstance(Class<? extends ResourceComponentProviderFactory> providerFactoryClass) {
        try {
            ComponentInjector<? extends ResourceComponentProviderFactory> ci = new ComponentInjector<ResourceComponentProviderFactory>(this.ipc, providerFactoryClass);
            ComponentConstructor<? extends ResourceComponentProviderFactory> cc = new ComponentConstructor<ResourceComponentProviderFactory>(this.ipc, providerFactoryClass, ci);
            return cc.getInstance();
        }
        catch (Exception ex) {
            throw new ContainerException("Unable to create resource component provider", ex);
        }
    }
}

