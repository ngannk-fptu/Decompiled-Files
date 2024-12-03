/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.AbstractDecoratedMap;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class LinkedMap<K, V>
extends AbstractDecoratedMap<K, V>
implements Serializable {
    transient LinkedEntry<K, V> head;
    protected final boolean accessOrder;

    public LinkedMap() {
        this(null, false);
    }

    public LinkedMap(boolean bl) {
        this(null, bl);
    }

    public LinkedMap(Map<? extends K, ? extends V> map) {
        this(map, false);
    }

    public LinkedMap(Map<? extends K, ? extends V> map, boolean bl) {
        super(map);
        this.accessOrder = bl;
    }

    public LinkedMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2) {
        this(map, map2, false);
    }

    public LinkedMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2, boolean bl) {
        super(map, map2);
        this.accessOrder = bl;
    }

    @Override
    protected void init() {
        this.head = new LinkedEntry<K, V>(null, null, null){

            @Override
            void addBefore(LinkedEntry linkedEntry) {
                throw new Error();
            }

            @Override
            void remove() {
                throw new Error();
            }

            @Override
            public void recordAccess(Map map) {
                throw new Error();
            }

            @Override
            public void recordRemoval(Map map) {
                throw new Error();
            }

            public void recordRemoval() {
                throw new Error();
            }

            @Override
            public V getValue() {
                throw new Error();
            }

            @Override
            public V setValue(V v) {
                throw new Error();
            }

            @Override
            public K getKey() {
                throw new Error();
            }

            @Override
            public String toString() {
                return "head";
            }
        };
        this.head.next = this.head;
        this.head.previous = this.head.next;
    }

    @Override
    public boolean containsValue(Object object) {
        if (object == null) {
            LinkedEntry linkedEntry = this.head.next;
            while (linkedEntry != this.head) {
                if (linkedEntry.mValue == null) {
                    return true;
                }
                linkedEntry = linkedEntry.next;
            }
        } else {
            LinkedEntry linkedEntry = this.head.next;
            while (linkedEntry != this.head) {
                if (object.equals(linkedEntry.mValue)) {
                    return true;
                }
                linkedEntry = linkedEntry.next;
            }
        }
        return false;
    }

    @Override
    protected Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    @Override
    protected Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    @Override
    protected Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    @Override
    public V get(Object object) {
        LinkedEntry linkedEntry = (LinkedEntry)this.entries.get(object);
        if (linkedEntry != null) {
            linkedEntry.recordAccess(this);
            return (V)linkedEntry.mValue;
        }
        return null;
    }

    @Override
    public V remove(Object object) {
        LinkedEntry linkedEntry = (LinkedEntry)this.entries.remove(object);
        if (linkedEntry != null) {
            linkedEntry.remove();
            ++this.modCount;
            return (V)linkedEntry.mValue;
        }
        return null;
    }

    @Override
    public V put(K k, V v) {
        Object object;
        Map.Entry<K, V> entry = (LinkedEntry)this.entries.get(k);
        if (entry == null) {
            object = null;
            LinkedEntry linkedEntry = this.head.next;
            if (this.removeEldestEntry(linkedEntry)) {
                this.removeEntry(linkedEntry);
            }
            entry = this.createEntry((Object)k, (Object)v);
            ((LinkedEntry)entry).addBefore(this.head);
            this.entries.put(k, entry);
        } else {
            object = ((LinkedEntry)entry).mValue;
            ((LinkedEntry)entry).mValue = v;
            ((LinkedEntry)entry).recordAccess(this);
        }
        ++this.modCount;
        return (V)object;
    }

    @Override
    LinkedEntry<K, V> createEntry(K k, V v) {
        return new LinkedEntry<K, V>(k, v, null);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        LinkedMap linkedMap = (LinkedMap)super.clone();
        return linkedMap;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return false;
    }

    protected static class LinkedEntry<K, V>
    extends AbstractDecoratedMap.BasicEntry<K, V>
    implements Serializable {
        LinkedEntry<K, V> previous;
        LinkedEntry<K, V> next;

        LinkedEntry(K k, V v, LinkedEntry<K, V> linkedEntry) {
            super(k, v);
            this.next = linkedEntry;
        }

        void addBefore(LinkedEntry<K, V> linkedEntry) {
            this.next = linkedEntry;
            this.previous = linkedEntry.previous;
            this.previous.next = this;
            this.next.previous = this;
        }

        void remove() {
            this.previous.next = this.next;
            this.next.previous = this.previous;
        }

        @Override
        protected void recordAccess(Map<K, V> map) {
            LinkedMap linkedMap = (LinkedMap)map;
            if (linkedMap.accessOrder) {
                ++linkedMap.modCount;
                this.remove();
                this.addBefore(linkedMap.head);
            }
        }

        @Override
        protected void recordRemoval(Map<K, V> map) {
            this.remove();
        }
    }

    private class EntryIterator
    extends LinkedMapIterator<Map.Entry<K, V>> {
        private EntryIterator() {
        }

        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    private class ValueIterator
    extends LinkedMapIterator<V> {
        private ValueIterator() {
        }

        @Override
        public V next() {
            return this.nextEntry().mValue;
        }
    }

    private class KeyIterator
    extends LinkedMapIterator<K> {
        private KeyIterator() {
        }

        @Override
        public K next() {
            return this.nextEntry().mKey;
        }
    }

    private abstract class LinkedMapIterator<E>
    implements Iterator<E> {
        LinkedEntry<K, V> mNextEntry;
        LinkedEntry<K, V> mLastReturned;
        int mExpectedModCount;

        private LinkedMapIterator() {
            this.mNextEntry = LinkedMap.this.head.next;
            this.mLastReturned = null;
            this.mExpectedModCount = LinkedMap.this.modCount;
        }

        @Override
        public boolean hasNext() {
            return this.mNextEntry != LinkedMap.this.head;
        }

        @Override
        public void remove() {
            if (this.mLastReturned == null) {
                throw new IllegalStateException();
            }
            if (LinkedMap.this.modCount != this.mExpectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkedMap.this.remove(this.mLastReturned.mKey);
            this.mLastReturned = null;
            this.mExpectedModCount = LinkedMap.this.modCount;
        }

        LinkedEntry<K, V> nextEntry() {
            if (LinkedMap.this.modCount != this.mExpectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.mNextEntry == LinkedMap.this.head) {
                throw new NoSuchElementException();
            }
            this.mLastReturned = this.mNextEntry;
            LinkedEntry linkedEntry = this.mLastReturned;
            this.mNextEntry = linkedEntry.next;
            return linkedEntry;
        }
    }
}

