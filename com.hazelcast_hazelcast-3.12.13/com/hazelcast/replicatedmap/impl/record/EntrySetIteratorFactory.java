/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.record.LazySet;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

class EntrySetIteratorFactory<K, V>
implements LazySet.IteratorFactory<K, V, Map.Entry<K, V>> {
    private final ReplicatedRecordStore recordStore;

    EntrySetIteratorFactory(ReplicatedRecordStore recordStore) {
        this.recordStore = recordStore;
    }

    @Override
    public Iterator<Map.Entry<K, V>> create(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
        return new EntrySetIterator(iterator);
    }

    private final class EntrySetIterator
    implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator;
        private Map.Entry<K, ReplicatedRecord<K, V>> entry;

        private EntrySetIterator(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            while (this.iterator.hasNext()) {
                this.entry = this.iterator.next();
                if (!this.testEntry(this.entry)) continue;
                return true;
            }
            return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            Object value;
            Map.Entry entry = this.entry;
            Object key = entry != null ? (Object)entry.getKey() : null;
            Object object = value = entry != null && entry.getValue() != null ? (Object)entry.getValue().getValue() : null;
            while (entry == null) {
                entry = this.findNextEntry();
                key = entry.getKey();
                ReplicatedRecord record = entry.getValue();
                Object object2 = value = record != null ? (Object)record.getValue() : null;
                if (key == null || value == null) continue;
                break;
            }
            this.entry = null;
            if (key == null || value == null) {
                throw new NoSuchElementException();
            }
            key = EntrySetIteratorFactory.this.recordStore.unmarshall(key);
            value = EntrySetIteratorFactory.this.recordStore.unmarshall(value);
            return new AbstractMap.SimpleEntry<Object, Object>(key, value);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Lazy structures are not modifiable");
        }

        private boolean testEntry(Map.Entry<K, ReplicatedRecord<K, V>> entry) {
            return entry.getKey() != null && entry.getValue() != null && !entry.getValue().isTombstone();
        }

        private Map.Entry<K, ReplicatedRecord<K, V>> findNextEntry() {
            Map.Entry entry = null;
            while (this.iterator.hasNext() && !this.testEntry(entry = this.iterator.next())) {
                entry = null;
            }
            if (entry == null) {
                throw new NoSuchElementException();
            }
            return entry;
        }
    }
}

