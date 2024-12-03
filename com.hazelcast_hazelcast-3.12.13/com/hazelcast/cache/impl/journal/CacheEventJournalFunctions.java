/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.journal.DeserializingEventJournalCacheEvent;
import com.hazelcast.cache.journal.EventJournalCacheEvent;
import com.hazelcast.internal.journal.DeserializingEntry;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.io.Serializable;
import java.util.Map;

public final class CacheEventJournalFunctions {
    private CacheEventJournalFunctions() {
    }

    public static <K, V> Predicate<EventJournalCacheEvent<K, V>> cachePutEvents() {
        return new CachePutEventsPredicate();
    }

    public static <K, V> Function<EventJournalCacheEvent<K, V>, Map.Entry<K, V>> cacheEventToEntry() {
        return new CacheEventToEntryProjection();
    }

    public static <K, V> Function<EventJournalCacheEvent<K, V>, V> cacheEventNewValue() {
        return new CacheEventNewValueProjection();
    }

    @SerializableByConvention
    private static class CacheEventNewValueProjection
    implements Function,
    Serializable {
        private static final long serialVersionUID = 1L;

        private CacheEventNewValueProjection() {
        }

        public Object apply(Object event) {
            DeserializingEventJournalCacheEvent casted = (DeserializingEventJournalCacheEvent)event;
            return casted.getDataNewValue();
        }
    }

    @SerializableByConvention
    private static class CacheEventToEntryProjection<K, V>
    implements Function<EventJournalCacheEvent<K, V>, Map.Entry<K, V>>,
    Serializable {
        private static final long serialVersionUID = 1L;

        private CacheEventToEntryProjection() {
        }

        @Override
        public Map.Entry<K, V> apply(EventJournalCacheEvent<K, V> e) {
            DeserializingEventJournalCacheEvent casted = (DeserializingEventJournalCacheEvent)e;
            return new DeserializingEntry(casted.getDataKey(), casted.getDataNewValue());
        }
    }

    @SerializableByConvention
    private static class CachePutEventsPredicate<K, V>
    implements Predicate<EventJournalCacheEvent<K, V>>,
    Serializable {
        private static final long serialVersionUID = 1L;

        private CachePutEventsPredicate() {
        }

        @Override
        public boolean test(EventJournalCacheEvent<K, V> e) {
            return e.getType() == CacheEventType.CREATED || e.getType() == CacheEventType.UPDATED;
        }
    }
}

