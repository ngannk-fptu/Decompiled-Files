/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.KeyComparator;
import com.sun.jersey.core.util.KeyComparatorHashMap;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class KeyComparatorLinkedHashMap<K, V>
extends KeyComparatorHashMap<K, V>
implements Map<K, V> {
    private static final long serialVersionUID = 3801124242820219131L;
    private transient Entry<K, V> header;
    private final boolean accessOrder;

    public KeyComparatorLinkedHashMap(int initialCapacity, float loadFactor, KeyComparator<K> keyComparator) {
        super(initialCapacity, loadFactor, keyComparator);
        this.accessOrder = false;
    }

    public KeyComparatorLinkedHashMap(int initialCapacity, KeyComparator<K> keyComparator) {
        super(initialCapacity, keyComparator);
        this.accessOrder = false;
    }

    public KeyComparatorLinkedHashMap(KeyComparator<K> keyComparator) {
        super(keyComparator);
        this.accessOrder = false;
    }

    public KeyComparatorLinkedHashMap(Map<? extends K, ? extends V> m, KeyComparator<K> keyComparator) {
        super(m, keyComparator);
        this.accessOrder = false;
    }

    public KeyComparatorLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder, KeyComparator<K> keyComparator) {
        super(initialCapacity, loadFactor, keyComparator);
        this.accessOrder = accessOrder;
    }

    @Override
    void init() {
        this.header = new Entry<Object, Object>(-1, null, null, null);
        this.header.after = this.header;
        this.header.before = this.header.after;
    }

    @Override
    void transfer(KeyComparatorHashMap.Entry[] newTable) {
        int newCapacity = newTable.length;
        Entry e = this.header.after;
        while (e != this.header) {
            int index = KeyComparatorLinkedHashMap.indexFor(e.hash, newCapacity);
            e.next = newTable[index];
            newTable[index] = e;
            e = e.after;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            Entry e = this.header.after;
            while (e != this.header) {
                if (e.value == null) {
                    return true;
                }
                e = e.after;
            }
        } else {
            Entry e = this.header.after;
            while (e != this.header) {
                if (value.equals(e.value)) {
                    return true;
                }
                e = e.after;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Entry e = (Entry)this.getEntry(key);
        if (e == null) {
            return null;
        }
        e.recordAccess(this);
        return (V)e.value;
    }

    @Override
    public void clear() {
        super.clear();
        this.header.after = this.header;
        this.header.before = this.header.after;
    }

    @Override
    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    @Override
    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    @Override
    Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    @Override
    void addEntry(int hash, K key, V value, int bucketIndex) {
        this.createEntry(hash, key, value, bucketIndex);
        Entry eldest = this.header.after;
        if (this.removeEldestEntry(eldest)) {
            this.removeEntryForKey(eldest.key);
        } else if (this.size >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }

    @Override
    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> e;
        KeyComparatorHashMap.Entry old = this.table[bucketIndex];
        this.table[bucketIndex] = e = new Entry<K, V>(hash, key, value, old);
        ((Entry)e).addBefore((Entry)this.header);
        ++this.size;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return false;
    }

    private class EntryIterator
    extends LinkedHashIterator<Map.Entry<K, V>> {
        private EntryIterator() {
        }

        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    private class ValueIterator
    extends LinkedHashIterator<V> {
        private ValueIterator() {
        }

        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }

    private class KeyIterator
    extends LinkedHashIterator<K> {
        private KeyIterator() {
        }

        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }

    private abstract class LinkedHashIterator<T>
    implements Iterator<T> {
        Entry<K, V> nextEntry;
        Entry<K, V> lastReturned;
        int expectedModCount;

        private LinkedHashIterator() {
            this.nextEntry = ((KeyComparatorLinkedHashMap)KeyComparatorLinkedHashMap.this).header.after;
            this.lastReturned = null;
            this.expectedModCount = KeyComparatorLinkedHashMap.this.modCount;
        }

        @Override
        public boolean hasNext() {
            return this.nextEntry != KeyComparatorLinkedHashMap.this.header;
        }

        @Override
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            if (KeyComparatorLinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            KeyComparatorLinkedHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
            this.expectedModCount = KeyComparatorLinkedHashMap.this.modCount;
        }

        Entry<K, V> nextEntry() {
            if (KeyComparatorLinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.nextEntry == KeyComparatorLinkedHashMap.this.header) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            Entry e = this.lastReturned;
            this.nextEntry = e.after;
            return e;
        }
    }

    private static class Entry<K, V>
    extends KeyComparatorHashMap.Entry<K, V> {
        Entry<K, V> before;
        Entry<K, V> after;

        Entry(int hash, K key, V value, KeyComparatorHashMap.Entry<K, V> next) {
            super(hash, key, value, next);
        }

        private void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }

        private void addBefore(Entry<K, V> existingEntry) {
            this.after = existingEntry;
            this.before = existingEntry.before;
            this.before.after = this;
            this.after.before = this;
        }

        @Override
        void recordAccess(KeyComparatorHashMap<K, V> m) {
            KeyComparatorLinkedHashMap lm = (KeyComparatorLinkedHashMap)m;
            if (lm.accessOrder) {
                ++lm.modCount;
                this.remove();
                this.addBefore(lm.header);
            }
        }

        @Override
        void recordRemoval(KeyComparatorHashMap<K, V> m) {
            this.remove();
        }
    }
}

