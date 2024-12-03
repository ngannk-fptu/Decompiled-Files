/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.journal.DeserializingEntry;
import com.hazelcast.map.impl.journal.DeserializingEventJournalMapEvent;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.io.Serializable;
import java.util.Map;

public final class MapEventJournalFunctions {
    private MapEventJournalFunctions() {
    }

    public static <K, V> Predicate<EventJournalMapEvent<K, V>> mapPutEvents() {
        return new MapPutEventsPredicate();
    }

    public static <K, V> Function<EventJournalMapEvent<K, V>, Map.Entry<K, V>> mapEventToEntry() {
        return new MapEventToEntryProjection();
    }

    public static <K, V> Function<EventJournalMapEvent<K, V>, V> mapEventNewValue() {
        return new MapEventNewValueProjection();
    }

    @SerializableByConvention
    private static class MapEventNewValueProjection
    implements Function,
    Serializable {
        private static final long serialVersionUID = 1L;

        private MapEventNewValueProjection() {
        }

        public Object apply(Object event) {
            DeserializingEventJournalMapEvent casted = (DeserializingEventJournalMapEvent)event;
            return casted.getDataNewValue();
        }
    }

    @SerializableByConvention
    private static class MapEventToEntryProjection<K, V>
    implements Function<EventJournalMapEvent<K, V>, Map.Entry<K, V>>,
    Serializable {
        private static final long serialVersionUID = 1L;

        private MapEventToEntryProjection() {
        }

        @Override
        public Map.Entry<K, V> apply(EventJournalMapEvent<K, V> e) {
            DeserializingEventJournalMapEvent casted = (DeserializingEventJournalMapEvent)e;
            return new DeserializingEntry(casted.getDataKey(), casted.getDataNewValue());
        }
    }

    @SerializableByConvention
    private static class MapPutEventsPredicate<K, V>
    implements Predicate<EventJournalMapEvent<K, V>>,
    Serializable {
        private static final long serialVersionUID = 1L;

        private MapPutEventsPredicate() {
        }

        @Override
        public boolean test(EventJournalMapEvent<K, V> e) {
            return e.getType() == EntryEventType.ADDED || e.getType() == EntryEventType.UPDATED;
        }
    }
}

