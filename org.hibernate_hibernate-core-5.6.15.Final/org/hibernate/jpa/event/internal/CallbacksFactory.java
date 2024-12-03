/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa.event.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.jpa.event.internal.CallbackRegistryImpl;
import org.hibernate.jpa.event.internal.CallbackRegistryImplementor;
import org.hibernate.jpa.event.internal.EmptyCallbackRegistryImpl;
import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.jpa.event.spi.CallbackDefinition;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.ServiceRegistry;
import org.jboss.logging.Logger;

public final class CallbacksFactory {
    private static final Logger log = Logger.getLogger(CallbacksFactory.class);

    public static CallbackRegistryImplementor buildCallbackRegistry(SessionFactoryOptions options, ServiceRegistry serviceRegistry, Collection<PersistentClass> entityBindings) {
        if (!CallbacksFactory.jpaCallBacksEnabled(options)) {
            return new EmptyCallbackRegistryImpl();
        }
        ManagedBeanRegistry beanRegistry = serviceRegistry.getService(ManagedBeanRegistry.class);
        CallbackRegistryImpl registry = new CallbackRegistryImpl();
        HashSet<Class> entityClasses = new HashSet<Class>();
        for (PersistentClass persistentClass : entityBindings) {
            if (persistentClass.getClassName() == null) continue;
            Class entityClass = persistentClass.getMappedClass();
            if (!entityClasses.add(entityClass)) {
                if (!log.isDebugEnabled()) continue;
                log.debugf("Class [%s] already has callbacks registered; assuming this means the class was mapped twice (using hbm.xml entity-name support) - skipping subsequent registrationsto avoid duplicates", (Object)entityClass.getName());
                continue;
            }
            registry.registerCallbacks(persistentClass.getMappedClass(), CallbacksFactory.buildCallbacks(persistentClass.getCallbackDefinitions(), beanRegistry));
            Iterator propertyIterator = persistentClass.getDeclaredPropertyIterator();
            while (propertyIterator.hasNext()) {
                Property property = (Property)propertyIterator.next();
                registry.registerCallbacks(persistentClass.getMappedClass(), CallbacksFactory.buildCallbacks(property.getCallbackDefinitions(), beanRegistry));
            }
        }
        return registry;
    }

    private static Callback[] buildCallbacks(List<CallbackDefinition> callbackDefinitions, ManagedBeanRegistry beanRegistry) {
        if (callbackDefinitions == null || callbackDefinitions.isEmpty()) {
            return null;
        }
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        for (CallbackDefinition definition : callbackDefinitions) {
            callbacks.add(definition.createCallback(beanRegistry));
        }
        return callbacks.toArray(new Callback[0]);
    }

    private static boolean jpaCallBacksEnabled(SessionFactoryOptions options) {
        return options.areJPACallbacksEnabled();
    }
}

