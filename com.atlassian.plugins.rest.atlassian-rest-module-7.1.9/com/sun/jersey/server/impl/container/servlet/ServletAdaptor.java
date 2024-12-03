/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceUnit
 *  javax.servlet.ServletConfig
 */
package com.sun.jersey.server.impl.container.servlet;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.ThreadLocalNamedInvoker;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletConfig;

public class ServletAdaptor
extends ServletContainer {
    private Map<String, String> persistenceUnits = new HashMap<String, String>();

    @Override
    protected void configure(ServletConfig servletConfig, ResourceConfig rc, WebApplication wa) {
        super.configure(servletConfig, rc, wa);
        Enumeration e = servletConfig.getInitParameterNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = servletConfig.getInitParameter(key);
            if (!key.startsWith("unit:")) continue;
            this.persistenceUnits.put(key.substring(5), "java:comp/env/" + value);
        }
        rc.getSingletons().add(new InjectableProvider<PersistenceUnit, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<EntityManagerFactory> getInjectable(ComponentContext ic, PersistenceUnit pu, Type c) {
                if (!c.equals(EntityManagerFactory.class)) {
                    return null;
                }
                if (!ServletAdaptor.this.persistenceUnits.containsKey(pu.unitName())) {
                    throw new ContainerException("Persistence unit '" + pu.unitName() + "' is not configured as a servlet parameter in web.xml");
                }
                String jndiName = (String)ServletAdaptor.this.persistenceUnits.get(pu.unitName());
                ThreadLocalNamedInvoker emfHandler = new ThreadLocalNamedInvoker(jndiName);
                final EntityManagerFactory emf = (EntityManagerFactory)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{EntityManagerFactory.class}, emfHandler);
                return new Injectable<EntityManagerFactory>(){

                    @Override
                    public EntityManagerFactory getValue() {
                        return emf;
                    }
                };
            }
        });
    }
}

