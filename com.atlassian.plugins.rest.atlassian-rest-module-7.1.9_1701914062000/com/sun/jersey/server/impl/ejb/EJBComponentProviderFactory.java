/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.Singleton
 *  javax.ejb.Stateless
 */
package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import com.sun.jersey.core.util.Priority;
import com.sun.jersey.server.impl.ejb.EJBInjectionInterceptor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Priority(value=300)
final class EJBComponentProviderFactory
implements IoCComponentProviderFactory,
IoCComponentProcessorFactoryInitializer {
    private static final Logger LOGGER = Logger.getLogger(EJBComponentProviderFactory.class.getName());
    private final EJBInjectionInterceptor interceptor;

    public EJBComponentProviderFactory(EJBInjectionInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c) {
        return this.getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> c) {
        String name = this.getName(c);
        if (name == null) {
            return null;
        }
        try {
            InitialContext ic = new InitialContext();
            Object o = this.lookup(ic, c, name);
            LOGGER.info("Binding the EJB class " + c.getName() + " to EJBManagedComponentProvider");
            return new EJBManagedComponentProvider(o);
        }
        catch (NamingException ex) {
            String message = "An instance of EJB class " + c.getName() + " could not be looked up using simple form name or the fully-qualified form name.Ensure that the EJB/JAX-RS component implements at most one interface.";
            LOGGER.log(Level.SEVERE, message, ex);
            throw new ContainerException(message);
        }
    }

    private String getName(Class<?> c) {
        String name = null;
        if (c.isAnnotationPresent(Stateless.class)) {
            name = c.getAnnotation(Stateless.class).name();
        } else if (c.isAnnotationPresent(Singleton.class)) {
            name = c.getAnnotation(Singleton.class).name();
        } else {
            return null;
        }
        if (name == null || name.length() == 0) {
            name = c.getSimpleName();
        }
        return name;
    }

    private Object lookup(InitialContext ic, Class<?> c, String name) throws NamingException {
        try {
            return this.lookupSimpleForm(ic, c, name);
        }
        catch (NamingException ex) {
            LOGGER.log(Level.WARNING, "An instance of EJB class " + c.getName() + " could not be looked up using simple form name. Attempting to look up using the fully-qualified form name.", ex);
            return this.lookupFullyQualfiedForm(ic, c, name);
        }
    }

    private Object lookupSimpleForm(InitialContext ic, Class<?> c, String name) throws NamingException {
        String jndiName = "java:module/" + name;
        return ic.lookup(jndiName);
    }

    private Object lookupFullyQualfiedForm(InitialContext ic, Class<?> c, String name) throws NamingException {
        String jndiName = "java:module/" + name + "!" + c.getName();
        return ic.lookup(jndiName);
    }

    @Override
    public void init(IoCComponentProcessorFactory cpf) {
        this.interceptor.setFactory(cpf);
    }

    private static class EJBManagedComponentProvider
    implements IoCFullyManagedComponentProvider {
        private final Object o;

        EJBManagedComponentProvider(Object o) {
            this.o = o;
        }

        @Override
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        @Override
        public Object getInstance() {
            return this.o;
        }
    }
}

