/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.AutoFlushEventListener;
import org.hibernate.event.spi.ClearEventListener;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.DirtyCheckEventListener;
import org.hibernate.event.spi.EvictEventListener;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.event.spi.LockEventListener;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PostCollectionRecreateEventListener;
import org.hibernate.event.spi.PostCollectionRemoveEventListener;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreCollectionRecreateEventListener;
import org.hibernate.event.spi.PreCollectionRemoveEventListener;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.RefreshEventListener;
import org.hibernate.event.spi.ReplicateEventListener;
import org.hibernate.event.spi.ResolveNaturalIdEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;

public final class EventType<T> {
    private static AtomicInteger STANDARD_TYPE_COUNTER = new AtomicInteger(0);
    public static final EventType<LoadEventListener> LOAD = EventType.create("load", LoadEventListener.class);
    public static final EventType<ResolveNaturalIdEventListener> RESOLVE_NATURAL_ID = EventType.create("resolve-natural-id", ResolveNaturalIdEventListener.class);
    public static final EventType<InitializeCollectionEventListener> INIT_COLLECTION = EventType.create("load-collection", InitializeCollectionEventListener.class);
    public static final EventType<SaveOrUpdateEventListener> SAVE_UPDATE = EventType.create("save-update", SaveOrUpdateEventListener.class);
    public static final EventType<SaveOrUpdateEventListener> UPDATE = EventType.create("update", SaveOrUpdateEventListener.class);
    public static final EventType<SaveOrUpdateEventListener> SAVE = EventType.create("save", SaveOrUpdateEventListener.class);
    public static final EventType<PersistEventListener> PERSIST = EventType.create("create", PersistEventListener.class);
    public static final EventType<PersistEventListener> PERSIST_ONFLUSH = EventType.create("create-onflush", PersistEventListener.class);
    public static final EventType<MergeEventListener> MERGE = EventType.create("merge", MergeEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = EventType.create("delete", DeleteEventListener.class);
    public static final EventType<ReplicateEventListener> REPLICATE = EventType.create("replicate", ReplicateEventListener.class);
    public static final EventType<FlushEventListener> FLUSH = EventType.create("flush", FlushEventListener.class);
    public static final EventType<AutoFlushEventListener> AUTO_FLUSH = EventType.create("auto-flush", AutoFlushEventListener.class);
    public static final EventType<DirtyCheckEventListener> DIRTY_CHECK = EventType.create("dirty-check", DirtyCheckEventListener.class);
    public static final EventType<FlushEntityEventListener> FLUSH_ENTITY = EventType.create("flush-entity", FlushEntityEventListener.class);
    public static final EventType<ClearEventListener> CLEAR = EventType.create("clear", ClearEventListener.class);
    public static final EventType<EvictEventListener> EVICT = EventType.create("evict", EvictEventListener.class);
    public static final EventType<LockEventListener> LOCK = EventType.create("lock", LockEventListener.class);
    public static final EventType<RefreshEventListener> REFRESH = EventType.create("refresh", RefreshEventListener.class);
    public static final EventType<PreLoadEventListener> PRE_LOAD = EventType.create("pre-load", PreLoadEventListener.class);
    public static final EventType<PreDeleteEventListener> PRE_DELETE = EventType.create("pre-delete", PreDeleteEventListener.class);
    public static final EventType<PreUpdateEventListener> PRE_UPDATE = EventType.create("pre-update", PreUpdateEventListener.class);
    public static final EventType<PreInsertEventListener> PRE_INSERT = EventType.create("pre-insert", PreInsertEventListener.class);
    public static final EventType<PostLoadEventListener> POST_LOAD = EventType.create("post-load", PostLoadEventListener.class);
    public static final EventType<PostDeleteEventListener> POST_DELETE = EventType.create("post-delete", PostDeleteEventListener.class);
    public static final EventType<PostUpdateEventListener> POST_UPDATE = EventType.create("post-update", PostUpdateEventListener.class);
    public static final EventType<PostInsertEventListener> POST_INSERT = EventType.create("post-insert", PostInsertEventListener.class);
    public static final EventType<PostDeleteEventListener> POST_COMMIT_DELETE = EventType.create("post-commit-delete", PostDeleteEventListener.class);
    public static final EventType<PostUpdateEventListener> POST_COMMIT_UPDATE = EventType.create("post-commit-update", PostUpdateEventListener.class);
    public static final EventType<PostInsertEventListener> POST_COMMIT_INSERT = EventType.create("post-commit-insert", PostInsertEventListener.class);
    public static final EventType<PreCollectionRecreateEventListener> PRE_COLLECTION_RECREATE = EventType.create("pre-collection-recreate", PreCollectionRecreateEventListener.class);
    public static final EventType<PreCollectionRemoveEventListener> PRE_COLLECTION_REMOVE = EventType.create("pre-collection-remove", PreCollectionRemoveEventListener.class);
    public static final EventType<PreCollectionUpdateEventListener> PRE_COLLECTION_UPDATE = EventType.create("pre-collection-update", PreCollectionUpdateEventListener.class);
    public static final EventType<PostCollectionRecreateEventListener> POST_COLLECTION_RECREATE = EventType.create("post-collection-recreate", PostCollectionRecreateEventListener.class);
    public static final EventType<PostCollectionRemoveEventListener> POST_COLLECTION_REMOVE = EventType.create("post-collection-remove", PostCollectionRemoveEventListener.class);
    public static final EventType<PostCollectionUpdateEventListener> POST_COLLECTION_UPDATE = EventType.create("post-collection-update", PostCollectionUpdateEventListener.class);
    private static final Map<String, EventType> STANDARD_TYPE_BY_NAME_MAP = AccessController.doPrivileged(new PrivilegedAction<Map<String, EventType>>(){

        @Override
        public Map<String, EventType> run() {
            HashMap<String, EventType> typeByNameMap = new HashMap<String, EventType>();
            for (Field field : EventType.class.getDeclaredFields()) {
                if (!EventType.class.isAssignableFrom(field.getType())) continue;
                try {
                    EventType typeField = (EventType)field.get(null);
                    typeByNameMap.put(typeField.eventName(), typeField);
                }
                catch (Exception t) {
                    throw new HibernateException("Unable to initialize EventType map", t);
                }
            }
            return Collections.unmodifiableMap(typeByNameMap);
        }
    });
    private final String eventName;
    private final Class<T> baseListenerInterface;
    private final int ordinal;
    private final boolean isStandardEvent;

    private static <T> EventType<T> create(String name, Class<T> listenerRole) {
        return new EventType<T>(name, listenerRole, STANDARD_TYPE_COUNTER.getAndIncrement(), true);
    }

    public static <T> EventType<T> create(String name, Class<T> listenerRole, int ordinal) {
        return new EventType<T>(name, listenerRole, ordinal, false);
    }

    public static EventType resolveEventTypeByName(String eventName) {
        if (eventName == null) {
            throw new HibernateException("event name to resolve cannot be null");
        }
        EventType eventType = STANDARD_TYPE_BY_NAME_MAP.get(eventName);
        if (eventType == null) {
            throw new HibernateException("Unable to locate proper event type for event name [" + eventName + "]");
        }
        return eventType;
    }

    public static Collection<EventType> values() {
        return STANDARD_TYPE_BY_NAME_MAP.values();
    }

    static void registerStandardTypes(Map<String, EventType> eventTypes) {
        eventTypes.putAll(STANDARD_TYPE_BY_NAME_MAP);
    }

    private EventType(String eventName, Class<T> baseListenerInterface, int ordinal, boolean isStandardEvent) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
        this.ordinal = ordinal;
        this.isStandardEvent = isStandardEvent;
    }

    public String eventName() {
        return this.eventName;
    }

    public Class baseListenerInterface() {
        return this.baseListenerInterface;
    }

    public int ordinal() {
        return this.ordinal;
    }

    public boolean isStandardEvent() {
        return this.isStandardEvent;
    }

    public String toString() {
        return this.eventName();
    }
}

