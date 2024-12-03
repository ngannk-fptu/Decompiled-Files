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

class KeySetIteratorFactory<K, V>
implements LazySet.IteratorFactory<K, V, K> {
    private final ReplicatedRecordStore recordStore;

    KeySetIteratorFactory(ReplicatedRecordStore recordStore) {
        this.recordStore = recordStore;
    }

    @Override
    public Iterator<K> create(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
        return new KeySetIterator(iterator);
    }

    private final class KeySetIterator
    implements Iterator<K> {
        private final Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator;
        private Map.Entry<K, ReplicatedRecord<K, V>> nextEntry;

        private KeySetIterator(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            while (this.iterator.hasNext()) {
                Map.Entry entry = this.iterator.next();
                if (!this.testEntry(entry)) continue;
                this.nextEntry = entry;
                return true;
            }
            return false;
        }

        @Override
        public K next() {
            Object key;
            Map.Entry entry = this.nextEntry;
            Object v0 = key = entry != null ? entry.getKey() : null;
            while (entry == null && (key = (entry = this.findNextEntry()).getKey()) == null) {
            }
            this.nextEntry = null;
            if (key == null) {
                throw new NoSuchElementException();
            }
            return KeySetIteratorFactory.this.recordStore.unmarshall(key);
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

