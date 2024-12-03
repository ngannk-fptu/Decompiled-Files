/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.internal.EventListenerRegistryImpl;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventEngineContributions;
import org.hibernate.event.spi.EventEngineContributor;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.jpa.event.internal.CallbackRegistryImplementor;
import org.hibernate.jpa.event.internal.CallbacksFactory;
import org.hibernate.service.spi.Stoppable;

public class EventEngine {
    private final Map<String, EventType> registeredEventTypes;
    private final EventListenerRegistry listenerRegistry;
    private final CallbackRegistryImplementor callbackRegistry;

    public EventEngine(MetadataImplementor mappings, SessionFactoryImplementor sessionFactory) {
        this.callbackRegistry = CallbacksFactory.buildCallbackRegistry(sessionFactory.getSessionFactoryOptions(), sessionFactory.getServiceRegistry(), mappings.getEntityBindings());
        final EventListenerRegistryImpl.Builder listenerRegistryBuilder = new EventListenerRegistryImpl.Builder(this.callbackRegistry, sessionFactory.getSessionFactoryOptions().isJpaBootstrap());
        final HashMap<String, EventType> eventTypes = new HashMap<String, EventType>();
        EventType.registerStandardTypes(eventTypes);
        EventEngineContributions contributionManager = new EventEngineContributions(){

            @Override
            public <T> EventType<T> findEventType(String name) {
                return (EventType)eventTypes.get(name);
            }

            @Override
            public <T> EventType<T> contributeEventType(String name, Class<T> listenerRole) {
                EventType<T> eventType = this.registerEventType(name, listenerRole);
                listenerRegistryBuilder.prepareListeners(eventType);
                return eventType;
            }

            private <T> EventType<T> registerEventType(String name, Class<T> listenerRole) {
                if (name == null) {
                    throw new HibernateException("Custom event-type name must be non-null.");
                }
                if (listenerRole == null) {
                    throw new HibernateException("Custom event-type listener role must be non-null.");
                }
                if (eventTypes.containsKey(name)) {
                    EventType existing = (EventType)eventTypes.get(name);
                    throw new HibernateException("Custom event-type already registered: " + name + " => " + existing);
                }
                EventType<T> eventType = EventType.create(name, listenerRole, eventTypes.size());
                eventTypes.put(name, eventType);
                return eventType;
            }

            @Override
            public <T> EventType<T> contributeEventType(String name, Class<T> listenerRole, T ... defaultListeners) {
                EventType<T> eventType = this.contributeEventType(name, listenerRole);
                if (defaultListeners != null) {
                    EventListenerGroup<T> listenerGroup = listenerRegistryBuilder.getListenerGroup(eventType);
                    listenerGroup.appendListeners(defaultListeners);
                }
                return eventType;
            }

            @Override
            public <T> void configureListeners(EventType<T> eventType, Consumer<EventListenerGroup<T>> action) {
                if (!eventTypes.containsValue(eventType)) {
                    throw new HibernateException("EventType [" + eventType + "] not registered");
                }
                action.accept(listenerRegistryBuilder.getListenerGroup(eventType));
            }
        };
        Collection<EventEngineContributor> discoveredContributors = sessionFactory.getServiceRegistry().getService(ClassLoaderService.class).loadJavaServices(EventEngineContributor.class);
        if (CollectionHelper.isNotEmpty(discoveredContributors)) {
            for (EventEngineContributor contributor : discoveredContributors) {
                contributor.contribute(contributionManager);
            }
        }
        this.registeredEventTypes = Collections.unmodifiableMap(eventTypes);
        this.listenerRegistry = listenerRegistryBuilder.buildRegistry(this.registeredEventTypes);
    }

    public Collection<EventType<?>> getRegisteredEventTypes() {
        return this.registeredEventTypes.values();
    }

    public <T> EventType<T> findRegisteredEventType(String name) {
        return this.registeredEventTypes.get(name);
    }

    public EventListenerRegistry getListenerRegistry() {
        return this.listenerRegistry;
    }

    public CallbackRegistryImplementor getCallbackRegistry() {
        return this.callbackRegistry;
    }

    public void stop() {
        if (this.listenerRegistry instanceof Stoppable) {
            ((Stoppable)((Object)this.listenerRegistry)).stop();
        }
        this.callbackRegistry.release();
    }
}

