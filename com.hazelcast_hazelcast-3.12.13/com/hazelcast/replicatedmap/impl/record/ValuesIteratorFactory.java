/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.record.LazySet;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

class ValuesIteratorFactory<K, V>
implements LazySet.IteratorFactory<K, V, V> {
    private final ReplicatedRecordStore recordStore;

    ValuesIteratorFactory(ReplicatedRecordStore recordStore) {
        this.recordStore = recordStore;
    }

    @Override
    public Iterator<V> create(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
        return new ValuesIterator(iterator);
    }

    private final class ValuesIterator
    implements Iterator<V> {
        private final Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator;
        private Map.Entry<K, ReplicatedRecord<K, V>> entry;

        ValuesIterator(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
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
        public V next() {
            ReplicatedRecord record;
            Object value;
            Map.Entry entry = this.entry;
            Object object = value = entry != null && entry.getValue() != null ? (Object)entry.getValue().getValue() : null;
            while (entry == null && (value = (record = (entry = this.findNextEntry()).getValue()) != null ? (Object)record.getValue() : null) == null) {
            }
            this.entry = null;
            if (value == null) {
                throw new NoSuchElementException();
            }
            value = ValuesIteratorFactory.this.recordStore.unmarshall(value);
            return value;
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

