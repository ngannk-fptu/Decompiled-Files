/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.service.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultAutoFlushEventListener;
import org.hibernate.event.internal.DefaultDeleteEventListener;
import org.hibernate.event.internal.DefaultDirtyCheckEventListener;
import org.hibernate.event.internal.DefaultEvictEventListener;
import org.hibernate.event.internal.DefaultFlushEntityEventListener;
import org.hibernate.event.internal.DefaultFlushEventListener;
import org.hibernate.event.internal.DefaultInitializeCollectionEventListener;
import org.hibernate.event.internal.DefaultLoadEventListener;
import org.hibernate.event.internal.DefaultLockEventListener;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.internal.DefaultPersistEventListener;
import org.hibernate.event.internal.DefaultPersistOnFlushEventListener;
import org.hibernate.event.internal.DefaultPostLoadEventListener;
import org.hibernate.event.internal.DefaultPreLoadEventListener;
import org.hibernate.event.internal.DefaultRefreshEventListener;
import org.hibernate.event.internal.DefaultReplicateEventListener;
import org.hibernate.event.internal.DefaultResolveNaturalIdEventListener;
import org.hibernate.event.internal.DefaultSaveEventListener;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.internal.DefaultUpdateEventListener;
import org.hibernate.event.internal.PostDeleteEventListenerStandardImpl;
import org.hibernate.event.internal.PostInsertEventListenerStandardImpl;
import org.hibernate.event.internal.PostUpdateEventListenerStandardImpl;
import org.hibernate.event.service.internal.EventListenerGroupImpl;
import org.hibernate.event.service.internal.PostCommitEventListenerGroupImpl;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistrationException;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.jpa.event.internal.CallbackRegistryImplementor;
import org.hibernate.service.spi.Stoppable;

public class EventListenerRegistryImpl
implements EventListenerRegistry,
Stoppable {
    private final EventListenerGroup[] eventListeners;
    private final Map<Class<?>, Object> listenerClassToInstanceMap = new HashMap();

    private EventListenerRegistryImpl(EventListenerGroup[] eventListeners) {
        this.eventListeners = eventListeners;
    }

    @Override
    public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType) {
        if (this.eventListeners.length < eventType.ordinal() + 1) {
            throw new HibernateException("Unable to find listeners for type [" + eventType.eventName() + "]");
        }
        EventListenerGroup listeners = this.eventListeners[eventType.ordinal()];
        if (listeners == null) {
            throw new HibernateException("Unable to find listeners for type [" + eventType.eventName() + "]");
        }
        return listeners;
    }

    @Override
    public void addDuplicationStrategy(DuplicationStrategy strategy) {
        for (EventListenerGroup group : this.eventListeners) {
            if (group == null) continue;
            group.addDuplicationStrategy(strategy);
        }
    }

    @Override
    @SafeVarargs
    public final <T> void setListeners(EventType<T> type, Class<? extends T> ... listenerClasses) {
        this.setListeners(type, this.resolveListenerInstances(type, listenerClasses));
    }

    private <T> T[] resolveListenerInstances(EventType<T> type, Class<? extends T> ... listenerClasses) {
        Object[] listeners = (Object[])Array.newInstance(type.baseListenerInterface(), listenerClasses.length);
        for (int i = 0; i < listenerClasses.length; ++i) {
            listeners[i] = this.resolveListenerInstance(listenerClasses[i]);
        }
        return listeners;
    }

    private <T> T resolveListenerInstance(Class<T> listenerClass) {
        Object listenerInstance = this.listenerClassToInstanceMap.get(listenerClass);
        if (listenerInstance == null) {
            listenerInstance = this.instantiateListener(listenerClass);
            this.listenerClassToInstanceMap.put(listenerClass, listenerInstance);
        }
        return (T)listenerInstance;
    }

    private <T> T instantiateListener(Class<T> listenerClass) {
        try {
            return listenerClass.newInstance();
        }
        catch (Exception e) {
            throw new EventListenerRegistrationException("Unable to instantiate specified event listener class: " + listenerClass.getName(), e);
        }
    }

    @Override
    @SafeVarargs
    public final <T> void setListeners(EventType<T> type, T ... listeners) {
        EventListenerGroup<T> registeredListeners = this.getEventListenerGroup(type);
        registeredListeners.clear();
        if (listeners != null) {
            for (T listener : listeners) {
                registeredListeners.appendListener(listener);
            }
        }
    }

    @Override
    @SafeVarargs
    public final <T> void appendListeners(EventType<T> type, Class<? extends T> ... listenerClasses) {
        this.appendListeners(type, this.resolveListenerInstances(type, listenerClasses));
    }

    @Override
    @SafeVarargs
    public final <T> void appendListeners(EventType<T> type, T ... listeners) {
        this.getEventListenerGroup(type).appendListeners(listeners);
    }

    @Override
    @SafeVarargs
    public final <T> void prependListeners(EventType<T> type, Class<? extends T> ... listenerClasses) {
        this.prependListeners(type, this.resolveListenerInstances(type, listenerClasses));
    }

    @Override
    @SafeVarargs
    public final <T> void prependListeners(EventType<T> type, T ... listeners) {
        this.getEventListenerGroup(type).prependListeners(listeners);
    }

    @Override
    @Deprecated
    public void stop() {
    }

    public static class Builder {
        private final CallbackRegistryImplementor callbackRegistry;
        private final boolean jpaBootstrap;
        private final Map<EventType<?>, EventListenerGroup<?>> listenerGroupMap = new TreeMap(Comparator.comparing(EventType::ordinal));

        public Builder(CallbackRegistryImplementor callbackRegistry, boolean jpaBootstrap) {
            this.callbackRegistry = callbackRegistry;
            this.jpaBootstrap = jpaBootstrap;
            this.applyStandardListeners();
        }

        private void applyStandardListeners() {
            this.prepareListeners(EventType.AUTO_FLUSH, new DefaultAutoFlushEventListener());
            this.prepareListeners(EventType.PERSIST, new DefaultPersistEventListener());
            this.prepareListeners(EventType.PERSIST_ONFLUSH, new DefaultPersistOnFlushEventListener());
            this.prepareListeners(EventType.DELETE, new DefaultDeleteEventListener());
            this.prepareListeners(EventType.DIRTY_CHECK, new DefaultDirtyCheckEventListener());
            this.prepareListeners(EventType.EVICT, new DefaultEvictEventListener());
            this.prepareListeners(EventType.CLEAR);
            this.prepareListeners(EventType.FLUSH, new DefaultFlushEventListener());
            this.prepareListeners(EventType.FLUSH_ENTITY, new DefaultFlushEntityEventListener());
            this.prepareListeners(EventType.LOAD, new DefaultLoadEventListener());
            this.prepareListeners(EventType.RESOLVE_NATURAL_ID, new DefaultResolveNaturalIdEventListener());
            this.prepareListeners(EventType.INIT_COLLECTION, new DefaultInitializeCollectionEventListener());
            this.prepareListeners(EventType.LOCK, new DefaultLockEventListener());
            this.prepareListeners(EventType.MERGE, new DefaultMergeEventListener());
            this.prepareListeners(EventType.PRE_COLLECTION_RECREATE);
            this.prepareListeners(EventType.PRE_COLLECTION_REMOVE);
            this.prepareListeners(EventType.PRE_COLLECTION_UPDATE);
            this.prepareListeners(EventType.PRE_DELETE);
            this.prepareListeners(EventType.PRE_INSERT);
            this.prepareListeners(EventType.PRE_LOAD, new DefaultPreLoadEventListener());
            this.prepareListeners(EventType.PRE_UPDATE);
            this.prepareListeners(EventType.POST_COLLECTION_RECREATE);
            this.prepareListeners(EventType.POST_COLLECTION_REMOVE);
            this.prepareListeners(EventType.POST_COLLECTION_UPDATE);
            this.prepareListeners(EventType.POST_COMMIT_DELETE);
            this.prepareListeners(EventType.POST_COMMIT_INSERT);
            this.prepareListeners(EventType.POST_COMMIT_UPDATE);
            this.prepareListeners(EventType.POST_DELETE, new PostDeleteEventListenerStandardImpl());
            this.prepareListeners(EventType.POST_INSERT, new PostInsertEventListenerStandardImpl());
            this.prepareListeners(EventType.POST_LOAD, new DefaultPostLoadEventListener());
            this.prepareListeners(EventType.POST_UPDATE, new PostUpdateEventListenerStandardImpl());
            this.prepareListeners(EventType.UPDATE, new DefaultUpdateEventListener());
            this.prepareListeners(EventType.REFRESH, new DefaultRefreshEventListener());
            this.prepareListeners(EventType.REPLICATE, new DefaultReplicateEventListener());
            this.prepareListeners(EventType.SAVE, new DefaultSaveEventListener());
            this.prepareListeners(EventType.SAVE_UPDATE, new DefaultSaveOrUpdateEventListener());
        }

        public <T> void prepareListeners(EventType<T> eventType) {
            this.prepareListeners(eventType, null);
        }

        public <T> void prepareListeners(EventType<T> type, T defaultListener) {
            this.prepareListeners(type, defaultListener, t -> {
                if (type == EventType.POST_COMMIT_DELETE || type == EventType.POST_COMMIT_INSERT || type == EventType.POST_COMMIT_UPDATE) {
                    return new PostCommitEventListenerGroupImpl(type, this.callbackRegistry, this.jpaBootstrap);
                }
                return new EventListenerGroupImpl(type, this.callbackRegistry, this.jpaBootstrap);
            });
        }

        public <T> void prepareListeners(EventType<T> type, T defaultListener, Function<EventType<T>, EventListenerGroupImpl<T>> groupCreator) {
            EventListenerGroupImpl<T> listenerGroup = groupCreator.apply(type);
            if (defaultListener != null) {
                listenerGroup.appendListener(defaultListener);
            }
            this.listenerGroupMap.put(type, listenerGroup);
        }

        public <T> EventListenerGroup<T> getListenerGroup(EventType<T> eventType) {
            return this.listenerGroupMap.get(eventType);
        }

        public EventListenerRegistry buildRegistry(Map<String, EventType> registeredEventTypes) {
            ArrayList<EventType> eventTypeList = new ArrayList<EventType>(registeredEventTypes.values());
            eventTypeList.sort(Comparator.comparing(EventType::ordinal));
            EventListenerGroup[] eventListeners = new EventListenerGroup[eventTypeList.size()];
            int previous = -1;
            int i = 0;
            while (i < eventTypeList.size()) {
                EventType eventType = eventTypeList.get(i);
                assert (i == eventType.ordinal());
                assert (i - 1 == previous);
                eventListeners[i] = this.listenerGroupMap.get(eventType);
                previous = i++;
            }
            return new EventListenerRegistryImpl(eventListeners);
        }
    }
}

