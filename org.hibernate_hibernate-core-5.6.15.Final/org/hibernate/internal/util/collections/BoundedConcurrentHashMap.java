/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedConcurrentHashMap<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    static final int DEFAULT_MAXIMUM_CAPACITY = 512;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int MAXIMUM_CAPACITY = 0x40000000;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    transient Set<K> keySet;
    transient Set<Map.Entry<K, V>> entrySet;
    transient Collection<V> values;

    private static int hash(int h) {
        h += h << 15 ^ 0xFFFFCD7D;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }

    final Segment<K, V> segmentFor(int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }

    public BoundedConcurrentHashMap(int capacity, int concurrencyLevel, Eviction evictionStrategy, EvictionListener<K, V> evictionListener) {
        int cap;
        int ssize;
        if (capacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        concurrencyLevel = Math.min(capacity / 2, concurrencyLevel);
        if (capacity < (concurrencyLevel = Math.max(concurrencyLevel, 1)) * 2 && capacity != 1) {
            throw new IllegalArgumentException("Maximum capacity has to be at least twice the concurrencyLevel");
        }
        if (evictionStrategy == null || evictionListener == null) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > 65536) {
            concurrencyLevel = 65536;
        }
        int sshift = 0;
        for (ssize = 1; ssize < concurrencyLevel; ssize <<= 1) {
            ++sshift;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        if (capacity > 0x40000000) {
            capacity = 0x40000000;
        }
        int c = capacity / ssize;
        for (cap = 1; cap < c; cap <<= 1) {
        }
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment<K, V>(cap, c, 0.75f, evictionStrategy, evictionListener);
        }
    }

    public BoundedConcurrentHashMap(int capacity, int concurrencyLevel) {
        this(capacity, concurrencyLevel, Eviction.LRU);
    }

    public BoundedConcurrentHashMap(int capacity, int concurrencyLevel, Eviction evictionStrategy) {
        this(capacity, concurrencyLevel, evictionStrategy, new NullEvictionListener());
    }

    public BoundedConcurrentHashMap(int capacity) {
        this(capacity, 16);
    }

    public BoundedConcurrentHashMap() {
        this(512, 16);
    }

    @Override
    public boolean isEmpty() {
        int i;
        Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            mc[i] = segments[i].modCount;
            mcsum += mc[i];
        }
        if (mcsum != 0) {
            for (i = 0; i < segments.length; ++i) {
                if (segments[i].count == 0 && mc[i] == segments[i].modCount) continue;
                return false;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        Segment<K, V>[] segments = this.segments;
        long sum = 0L;
        long check = 0L;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            int i;
            check = 0L;
            sum = 0L;
            int mcsum = 0;
            for (i = 0; i < segments.length; ++i) {
                sum += (long)segments[i].count;
                mc[i] = segments[i].modCount;
                mcsum += mc[i];
            }
            if (mcsum != 0) {
                for (i = 0; i < segments.length; ++i) {
                    check += (long)segments[i].count;
                    if (mc[i] == segments[i].modCount) continue;
                    check = -1L;
                    break;
                }
            }
            if (check == sum) break;
        }
        if (check != sum) {
            int i;
            sum = 0L;
            for (i = 0; i < segments.length; ++i) {
                segments[i].lock();
            }
            try {
                for (i = 0; i < segments.length; ++i) {
                    sum += (long)segments[i].count;
                }
            }
            finally {
                for (i = 0; i < segments.length; ++i) {
                    segments[i].unlock();
                }
            }
        }
        if (sum > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }

    @Override
    public V get(Object key) {
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).get(key, hash);
    }

    @Override
    public boolean containsKey(Object key) {
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).containsKey(key, hash);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(Object value) {
        int i;
        if (value == null) {
            throw new NullPointerException();
        }
        Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            int mcsum = 0;
            for (int i2 = 0; i2 < segments.length; ++i2) {
                int c = segments[i2].count;
                mc[i2] = segments[i2].modCount;
                mcsum += mc[i2];
                if (!segments[i2].containsValue(value)) continue;
                return true;
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int i3 = 0; i3 < segments.length; ++i3) {
                    int c = segments[i3].count;
                    if (mc[i3] == segments[i3].modCount) continue;
                    cleanSweep = false;
                    break;
                }
            }
            if (!cleanSweep) continue;
            return false;
        }
        for (int i4 = 0; i4 < segments.length; ++i4) {
            segments[i4].lock();
        }
        boolean found = false;
        try {
            for (i = 0; i < segments.length; ++i) {
                if (!segments[i].containsValue(value)) continue;
                found = true;
                break;
            }
        }
        finally {
            for (i = 0; i < segments.length; ++i) {
                segments[i].unlock();
            }
        }
        return found;
    }

    public boolean contains(Object value) {
        return this.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, value, false);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, value, true);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<K, V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).remove(key, hash, null);
    }

    @Override
    public boolean remove(Object key, Object value) {
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        if (value == null) {
            return false;
        }
        return this.segmentFor(hash).remove(key, hash, value) != null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = BoundedConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, value);
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].clear();
        }
    }

    @Override
    public Set<K> keySet() {
        KeySet ks = this.keySet;
        return ks != null ? ks : (this.keySet = new KeySet());
    }

    @Override
    public Collection<V> values() {
        Values vs = this.values;
        return vs != null ? vs : (this.values = new Values());
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = this.entrySet;
        return es != null ? es : (this.entrySet = new EntrySet());
    }

    public Enumeration<K> keys() {
        return new KeyIterator();
    }

    public Enumeration<V> elements() {
        return new ValueIterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int k = 0; k < this.segments.length; ++k) {
            Segment<K, V> seg = this.segments[k];
            seg.lock();
            try {
                HashEntry<K, V>[] tab = seg.table;
                for (int i = 0; i < tab.length; ++i) {
                    HashEntry e = tab[i];
                    while (e != null) {
                        s.writeObject(e.key);
                        s.writeObject(e.value);
                        e = e.next;
                    }
                }
                continue;
            }
            finally {
                seg.unlock();
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].setTable(new HashEntry[1]);
        }
        while (true) {
            Object key = s.readObject();
            Object value = s.readObject();
            if (key == null) break;
            this.put(key, value);
        }
    }

    final class EntrySet
    extends AbstractSet<Map.Entry<K, V>> {
        EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object v = BoundedConcurrentHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return BoundedConcurrentHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override
        public int size() {
            return BoundedConcurrentHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return BoundedConcurrentHashMap.this.isEmpty();
        }

        @Override
        public void clear() {
            BoundedConcurrentHashMap.this.clear();
        }
    }

    final class Values
    extends AbstractCollection<V> {
        Values() {
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return BoundedConcurrentHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return BoundedConcurrentHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return BoundedConcurrentHashMap.this.containsValue(o);
        }

        @Override
        public void clear() {
            BoundedConcurrentHashMap.this.clear();
        }
    }

    final class KeySet
    extends AbstractSet<K> {
        KeySet() {
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return BoundedConcurrentHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return BoundedConcurrentHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return BoundedConcurrentHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return BoundedConcurrentHashMap.this.remove(o) != null;
        }

        @Override
        public void clear() {
            BoundedConcurrentHashMap.this.clear();
        }
    }

    final class EntryIterator
    extends HashIterator
    implements Iterator<Map.Entry<K, V>> {
        EntryIterator() {
        }

        @Override
        public Map.Entry<K, V> next() {
            HashEntry e = super.nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }

    final class WriteThroughEntry
    extends AbstractMap.SimpleEntry<K, V> {
        private static final long serialVersionUID = -7041346694785573824L;

        WriteThroughEntry(K k, V v) {
            super(k, v);
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            Object v = super.setValue(value);
            BoundedConcurrentHashMap.this.put(this.getKey(), value);
            return v;
        }
    }

    final class ValueIterator
    extends HashIterator
    implements Iterator<V>,
    Enumeration<V> {
        ValueIterator() {
        }

        @Override
        public V next() {
            return super.nextEntry().value;
        }

        @Override
        public V nextElement() {
            return super.nextEntry().value;
        }
    }

    final class KeyIterator
    extends HashIterator
    implements Iterator<K>,
    Enumeration<K> {
        KeyIterator() {
        }

        @Override
        public K next() {
            return super.nextEntry().key;
        }

        @Override
        public K nextElement() {
            return super.nextEntry().key;
        }
    }

    abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;

        HashIterator() {
            this.nextSegmentIndex = BoundedConcurrentHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }

        public boolean hasMoreElements() {
            return this.hasNext();
        }

        final void advance() {
            if (this.nextEntry != null && (this.nextEntry = this.nextEntry.next) != null) {
                return;
            }
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) == null) continue;
                return;
            }
            while (this.nextSegmentIndex >= 0) {
                Segment seg = BoundedConcurrentHashMap.this.segments[this.nextSegmentIndex--];
                if (seg.count == 0) continue;
                this.currentTable = seg.table;
                for (int j = this.currentTable.length - 1; j >= 0; --j) {
                    this.nextEntry = this.currentTable[j];
                    if (this.nextEntry == null) continue;
                    this.nextTableIndex = j - 1;
                    return;
                }
            }
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashEntry<K, V> nextEntry() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            this.advance();
            return this.lastReturned;
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            BoundedConcurrentHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }

    static final class Segment<K, V>
    extends ReentrantLock {
        private static final long serialVersionUID = 2249069246763182397L;
        volatile transient int count;
        transient int modCount;
        transient int threshold;
        volatile transient HashEntry<K, V>[] table;
        final float loadFactor;
        final int evictCap;
        final transient EvictionPolicy<K, V> eviction;
        final transient EvictionListener<K, V> evictionListener;

        Segment(int cap, int evictCap, float lf, Eviction es, EvictionListener<K, V> listener) {
            this.loadFactor = lf;
            this.evictCap = evictCap;
            this.eviction = es.make(this, evictCap, lf);
            this.evictionListener = listener;
            this.setTable(HashEntry.newArray(cap));
        }

        static <K, V> Segment<K, V>[] newArray(int i) {
            return new Segment[i];
        }

        EvictionListener<K, V> getEvictionListener() {
            return this.evictionListener;
        }

        void setTable(HashEntry<K, V>[] newTable) {
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            this.table = newTable;
        }

        HashEntry<K, V> getFirst(int hash) {
            HashEntry<K, V>[] tab = this.table;
            return tab[hash & tab.length - 1];
        }

        V readValueUnderLock(HashEntry<K, V> e) {
            this.lock();
            try {
                Object v = e.value;
                return v;
            }
            finally {
                this.unlock();
            }
        }

        V get(Object key, int hash) {
            int c = this.count;
            if (c != 0) {
                V result = null;
                HashEntry<K, V> e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        Object v = e.value;
                        if (v != null) {
                            result = v;
                            break;
                        }
                        result = this.readValueUnderLock(e);
                        break;
                    }
                    e = e.next;
                }
                if (result != null && this.eviction.onEntryHit(e)) {
                    Set<HashEntry<K, V>> evicted = this.attemptEviction(false);
                    this.notifyEvictionListener(evicted);
                }
                return result;
            }
            return null;
        }

        boolean containsKey(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V> e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        return true;
                    }
                    e = e.next;
                }
            }
            return false;
        }

        boolean containsValue(Object value) {
            if (this.count != 0) {
                for (HashEntry<K, V> e : this.table) {
                    while (e != null) {
                        Object v = e.value;
                        if (v == null) {
                            v = this.readValueUnderLock(e);
                        }
                        if (value.equals(v)) {
                            return true;
                        }
                        e = e.next;
                    }
                }
            }
            return false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        boolean replace(K key, int hash, V oldValue, V newValue) {
            this.lock();
            Set<HashEntry<K, V>> evicted = null;
            try {
                HashEntry<K, V> e = this.getFirst(hash);
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value)) {
                    replaced = true;
                    e.value = newValue;
                    if (this.eviction.onEntryHit(e)) {
                        evicted = this.attemptEviction(true);
                    }
                }
                boolean bl = replaced;
                return bl;
            }
            finally {
                this.unlock();
                this.notifyEvictionListener(evicted);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        V replace(K key, int hash, V newValue) {
            this.lock();
            Set<HashEntry<K, V>> evicted = null;
            try {
                HashEntry<K, V> e = this.getFirst(hash);
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value;
                    e.value = newValue;
                    if (this.eviction.onEntryHit(e)) {
                        evicted = this.attemptEviction(true);
                    }
                }
                V v = oldValue;
                return v;
            }
            finally {
                this.unlock();
                this.notifyEvictionListener(evicted);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            this.lock();
            Set<HashEntry<K, V>> evicted = null;
            try {
                V oldValue;
                HashEntry<K, V> first;
                int c = this.count;
                if (c++ > this.threshold && this.eviction.strategy() == Eviction.NONE) {
                    this.rehash();
                }
                HashEntry<K, V>[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry<K, V> e = first = tab[index];
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                if (e != null) {
                    oldValue = e.value;
                    if (!onlyIfAbsent) {
                        e.value = value;
                        this.eviction.onEntryHit(e);
                    }
                } else {
                    oldValue = null;
                    ++this.modCount;
                    this.count = c;
                    if (this.eviction.strategy() != Eviction.NONE) {
                        if (c > this.evictCap) {
                            evicted = this.eviction.execute();
                            first = tab[index];
                        }
                        tab[index] = this.eviction.createNewEntry(key, hash, first, value);
                        Set<HashEntry<K, V>> newlyEvicted = this.eviction.onEntryMiss(tab[index]);
                        if (!newlyEvicted.isEmpty()) {
                            if (evicted != null) {
                                evicted.addAll(newlyEvicted);
                            } else {
                                evicted = newlyEvicted;
                            }
                        }
                    } else {
                        tab[index] = this.eviction.createNewEntry(key, hash, first, value);
                    }
                }
                V v = oldValue;
                return v;
            }
            finally {
                this.unlock();
                this.notifyEvictionListener(evicted);
            }
        }

        void rehash() {
            HashEntry<K, V>[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= 0x40000000) {
                return;
            }
            HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            int sizeMask = newTable.length - 1;
            for (int i = 0; i < oldCapacity; ++i) {
                int k;
                HashEntry<K, V> e = oldTable[i];
                if (e == null) continue;
                HashEntry next = e.next;
                int idx = e.hash & sizeMask;
                if (next == null) {
                    newTable[idx] = e;
                    continue;
                }
                HashEntry<K, V> lastRun = e;
                int lastIdx = idx;
                HashEntry last = next;
                while (last != null) {
                    k = last.hash & sizeMask;
                    if (k != lastIdx) {
                        lastIdx = k;
                        lastRun = last;
                    }
                    last = last.next;
                }
                newTable[lastIdx] = lastRun;
                HashEntry<K, V> p = e;
                while (p != lastRun) {
                    k = p.hash & sizeMask;
                    HashEntry n = newTable[k];
                    newTable[k] = this.eviction.createNewEntry(p.key, p.hash, n, p.value);
                    p = p.next;
                }
            }
            this.table = newTable;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        V remove(Object key, int hash, Object value) {
            this.lock();
            try {
                HashEntry<K, V> first;
                int c = this.count - 1;
                HashEntry<K, V>[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry<K, V> e = first = tab[index];
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    Object v = e.value;
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        ++this.modCount;
                        this.eviction.onEntryRemove(e);
                        HashEntry newFirst = e.next;
                        HashEntry<K, V> p = first;
                        while (p != e) {
                            this.eviction.onEntryRemove(p);
                            newFirst = this.eviction.createNewEntry(p.key, p.hash, newFirst, p.value);
                            this.eviction.onEntryMiss(newFirst);
                            p = p.next;
                        }
                        tab[index] = newFirst;
                        this.count = c;
                    }
                }
                V v = oldValue;
                return v;
            }
            finally {
                this.unlock();
            }
        }

        void clear() {
            if (this.count != 0) {
                this.lock();
                try {
                    HashEntry<K, V>[] tab = this.table;
                    for (int i = 0; i < tab.length; ++i) {
                        tab[i] = null;
                    }
                    ++this.modCount;
                    this.eviction.clear();
                    this.count = 0;
                }
                finally {
                    this.unlock();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Set<HashEntry<K, V>> attemptEviction(boolean lockedAlready) {
            boolean obtainedLock;
            Set<HashEntry<K, V>> evicted = null;
            boolean bl = obtainedLock = lockedAlready || this.tryLock();
            if (!obtainedLock && this.eviction.thresholdExpired()) {
                this.lock();
                obtainedLock = true;
            }
            if (obtainedLock) {
                try {
                    if (this.eviction.thresholdExpired()) {
                        evicted = this.eviction.execute();
                    }
                }
                finally {
                    if (!lockedAlready) {
                        this.unlock();
                    }
                }
            }
            return evicted;
        }

        private void notifyEvictionListener(Set<HashEntry<K, V>> evicted) {
            if (evicted != null) {
                Map evictedCopy;
                if (evicted.size() == 1) {
                    HashEntry<K, V> evictedEntry = evicted.iterator().next();
                    evictedCopy = Collections.singletonMap(evictedEntry.key, evictedEntry.value);
                } else {
                    evictedCopy = new HashMap(evicted.size());
                    for (HashEntry<K, V> he : evicted) {
                        evictedCopy.put(he.key, he.value);
                    }
                    evictedCopy = Collections.unmodifiableMap(evictedCopy);
                }
                this.evictionListener.onEntryEviction(evictedCopy);
            }
        }
    }

    static final class LIRS<K, V>
    implements EvictionPolicy<K, V> {
        private static final float L_LIRS = 0.95f;
        private final Segment<K, V> segment;
        private final ConcurrentLinkedQueue<LIRSHashEntry<K, V>> accessQueue;
        private final int maxBatchQueueSize;
        private int size;
        private final float batchThresholdFactor;
        private final LIRSHashEntry<K, V> header = new LIRSHashEntry<Object, Object>(null, null, 0, null, null);
        private final int maximumHotSize;
        private final int maximumSize;
        private int hotSize;

        public LIRS(Segment<K, V> s, int capacity, int maxBatchSize, float batchThresholdFactor) {
            this.segment = s;
            this.maximumSize = capacity;
            this.maximumHotSize = LIRS.calculateLIRSize(capacity);
            this.maxBatchQueueSize = maxBatchSize > 64 ? 64 : maxBatchSize;
            this.batchThresholdFactor = batchThresholdFactor;
            this.accessQueue = new ConcurrentLinkedQueue();
        }

        private static int calculateLIRSize(int maximumSize) {
            int result = (int)(0.95f * (float)maximumSize);
            return result == maximumSize ? maximumSize - 1 : result;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<HashEntry<K, V>> execute() {
            HashSet<HashEntry<K, V>> evicted = new HashSet<HashEntry<K, V>>();
            try {
                for (LIRSHashEntry<K, V> e : this.accessQueue) {
                    if (!e.isResident()) continue;
                    e.hit(evicted);
                }
                this.removeFromSegment(evicted);
            }
            finally {
                this.accessQueue.clear();
            }
            return evicted;
        }

        private void pruneStack(Set<HashEntry<K, V>> evicted) {
            LIRSHashEntry<K, V> bottom = this.stackBottom();
            while (bottom != null && bottom.state != Recency.LIR_RESIDENT) {
                ((LIRSHashEntry)bottom).removeFromStack();
                if (bottom.state == Recency.HIR_NONRESIDENT) {
                    evicted.add(bottom);
                }
                bottom = this.stackBottom();
            }
        }

        @Override
        public Set<HashEntry<K, V>> onEntryMiss(HashEntry<K, V> en) {
            LIRSHashEntry e = (LIRSHashEntry)en;
            Set evicted = e.miss();
            this.removeFromSegment(evicted);
            return evicted;
        }

        private void removeFromSegment(Set<HashEntry<K, V>> evicted) {
            for (HashEntry<K, V> e : evicted) {
                ((LIRSHashEntry)e).evict();
                this.segment.evictionListener.onEntryChosenForEviction(e.value);
                this.segment.remove(e.key, e.hash, null);
            }
        }

        @Override
        public boolean onEntryHit(HashEntry<K, V> e) {
            this.accessQueue.add((LIRSHashEntry)e);
            return (float)this.accessQueue.size() >= (float)this.maxBatchQueueSize * this.batchThresholdFactor;
        }

        @Override
        public boolean thresholdExpired() {
            return this.accessQueue.size() >= this.maxBatchQueueSize;
        }

        @Override
        public void onEntryRemove(HashEntry<K, V> e) {
            ((LIRSHashEntry)e).remove();
            while (this.accessQueue.remove(e)) {
            }
        }

        @Override
        public void clear() {
            this.accessQueue.clear();
        }

        @Override
        public Eviction strategy() {
            return Eviction.LIRS;
        }

        private LIRSHashEntry<K, V> stackBottom() {
            LIRSHashEntry bottom = ((LIRSHashEntry)this.header).previousInStack;
            return bottom == this.header ? null : bottom;
        }

        private LIRSHashEntry<K, V> queueFront() {
            LIRSHashEntry front = ((LIRSHashEntry)this.header).nextInQueue;
            return front == this.header ? null : front;
        }

        private LIRSHashEntry<K, V> queueEnd() {
            LIRSHashEntry end = ((LIRSHashEntry)this.header).previousInQueue;
            return end == this.header ? null : end;
        }

        @Override
        public HashEntry<K, V> createNewEntry(K key, int hash, HashEntry<K, V> next, V value) {
            return new LIRSHashEntry<K, V>(this, key, hash, next, value);
        }
    }

    private static final class LIRSHashEntry<K, V>
    extends HashEntry<K, V> {
        private LIRSHashEntry<K, V> previousInStack;
        private LIRSHashEntry<K, V> nextInStack;
        private LIRSHashEntry<K, V> previousInQueue;
        private LIRSHashEntry<K, V> nextInQueue;
        volatile Recency state;
        LIRS<K, V> owner;

        LIRSHashEntry(LIRS<K, V> owner, K key, int hash, HashEntry<K, V> next, V value) {
            super(key, hash, next, value);
            this.owner = owner;
            this.state = Recency.HIR_RESIDENT;
            this.previousInStack = this;
            this.nextInStack = this;
            this.previousInQueue = this;
            this.nextInQueue = this;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = result * 31 + this.hash;
            result = result * 31 + this.key.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            HashEntry other = (HashEntry)o;
            return this.hash == other.hash && this.key.equals(other.key);
        }

        public boolean inStack() {
            return this.nextInStack != null;
        }

        public boolean inQueue() {
            return this.nextInQueue != null;
        }

        public void hit(Set<HashEntry<K, V>> evicted) {
            switch (this.state) {
                case LIR_RESIDENT: {
                    this.hotHit(evicted);
                    break;
                }
                case HIR_RESIDENT: {
                    this.coldHit(evicted);
                    break;
                }
                case HIR_NONRESIDENT: {
                    throw new IllegalStateException("Can't hit a non-resident entry!");
                }
                default: {
                    throw new AssertionError((Object)("Hit with unknown status: " + (Object)((Object)this.state)));
                }
            }
        }

        private void hotHit(Set<HashEntry<K, V>> evicted) {
            boolean onBottom = ((LIRS)this.owner).stackBottom() == this;
            this.moveToStackTop();
            if (onBottom) {
                ((LIRS)this.owner).pruneStack(evicted);
            }
        }

        private void coldHit(Set<HashEntry<K, V>> evicted) {
            boolean inStack = this.inStack();
            this.moveToStackTop();
            if (inStack) {
                this.hot();
                this.removeFromQueue();
                ((LIRS)this.owner).stackBottom().migrateToQueue();
                ((LIRS)this.owner).pruneStack(evicted);
            } else {
                this.moveToQueueEnd();
            }
        }

        private Set<HashEntry<K, V>> miss() {
            Set<HashEntry<K, V>> evicted = Collections.emptySet();
            if (((LIRS)this.owner).hotSize < ((LIRS)this.owner).maximumHotSize) {
                this.warmupMiss();
            } else {
                evicted = new HashSet();
                this.fullMiss(evicted);
            }
            ((LIRS)this.owner).size++;
            return evicted;
        }

        private void warmupMiss() {
            this.hot();
            this.moveToStackTop();
        }

        private void fullMiss(Set<HashEntry<K, V>> evicted) {
            if (((LIRS)this.owner).size >= ((LIRS)this.owner).maximumSize) {
                LIRSHashEntry evictedNode = ((LIRS)this.owner).queueFront();
                evicted.add(evictedNode);
            }
            boolean inStack = this.inStack();
            this.moveToStackTop();
            if (inStack) {
                this.hot();
                ((LIRS)this.owner).stackBottom().migrateToQueue();
                ((LIRS)this.owner).pruneStack(evicted);
            } else {
                this.cold();
            }
        }

        private void hot() {
            if (this.state != Recency.LIR_RESIDENT) {
                ((LIRS)this.owner).hotSize++;
            }
            this.state = Recency.LIR_RESIDENT;
        }

        private void cold() {
            if (this.state == Recency.LIR_RESIDENT) {
                ((LIRS)this.owner).hotSize--;
            }
            this.state = Recency.HIR_RESIDENT;
            this.moveToQueueEnd();
        }

        private void nonResident() {
            switch (this.state) {
                case LIR_RESIDENT: {
                    ((LIRS)this.owner).hotSize--;
                }
                case HIR_RESIDENT: {
                    ((LIRS)this.owner).size--;
                }
            }
            this.state = Recency.HIR_NONRESIDENT;
        }

        public boolean isResident() {
            return this.state != Recency.HIR_NONRESIDENT;
        }

        private void tempRemoveFromStack() {
            if (this.inStack()) {
                this.previousInStack.nextInStack = this.nextInStack;
                this.nextInStack.previousInStack = this.previousInStack;
            }
        }

        private void removeFromStack() {
            this.tempRemoveFromStack();
            this.previousInStack = null;
            this.nextInStack = null;
        }

        private void addToStackBefore(LIRSHashEntry<K, V> existingEntry) {
            this.previousInStack = existingEntry.previousInStack;
            this.nextInStack = existingEntry;
            this.previousInStack.nextInStack = this;
            this.nextInStack.previousInStack = this;
        }

        private void moveToStackTop() {
            this.tempRemoveFromStack();
            this.addToStackBefore(((LIRS)this.owner).header.nextInStack);
        }

        private void moveToStackBottom() {
            this.tempRemoveFromStack();
            this.addToStackBefore(((LIRS)this.owner).header);
        }

        private void tempRemoveFromQueue() {
            if (this.inQueue()) {
                this.previousInQueue.nextInQueue = this.nextInQueue;
                this.nextInQueue.previousInQueue = this.previousInQueue;
            }
        }

        private void removeFromQueue() {
            this.tempRemoveFromQueue();
            this.previousInQueue = null;
            this.nextInQueue = null;
        }

        private void addToQueueBefore(LIRSHashEntry<K, V> existingEntry) {
            this.previousInQueue = existingEntry.previousInQueue;
            this.nextInQueue = existingEntry;
            this.previousInQueue.nextInQueue = this;
            this.nextInQueue.previousInQueue = this;
        }

        private void moveToQueueEnd() {
            this.tempRemoveFromQueue();
            this.addToQueueBefore(((LIRS)this.owner).header);
        }

        private void migrateToQueue() {
            this.removeFromStack();
            this.cold();
        }

        private void migrateToStack() {
            this.removeFromQueue();
            if (!this.inStack()) {
                this.moveToStackBottom();
            }
            this.hot();
        }

        private void evict() {
            this.removeFromQueue();
            this.removeFromStack();
            this.nonResident();
            this.owner = null;
        }

        private V remove() {
            boolean wasHot = this.state == Recency.LIR_RESIDENT;
            Object result = this.value;
            LIRSHashEntry end = this.owner != null ? ((LIRS)this.owner).queueEnd() : null;
            this.evict();
            if (wasHot && end != null) {
                end.migrateToStack();
            }
            return (V)result;
        }
    }

    static final class LRU<K, V>
    extends LinkedHashMap<HashEntry<K, V>, V>
    implements EvictionPolicy<K, V> {
        private static final long serialVersionUID = -7645068174197717838L;
        private final ConcurrentLinkedQueue<HashEntry<K, V>> accessQueue;
        private final Segment<K, V> segment;
        private final int maxBatchQueueSize;
        private final int trimDownSize;
        private final float batchThresholdFactor;
        private final Set<HashEntry<K, V>> evicted;

        public LRU(Segment<K, V> s, int capacity, float lf, int maxBatchSize, float batchThresholdFactor) {
            super(capacity, lf, true);
            this.segment = s;
            this.trimDownSize = capacity;
            this.maxBatchQueueSize = maxBatchSize > 64 ? 64 : maxBatchSize;
            this.batchThresholdFactor = batchThresholdFactor;
            this.accessQueue = new ConcurrentLinkedQueue();
            this.evicted = new HashSet<HashEntry<K, V>>();
        }

        @Override
        public Set<HashEntry<K, V>> execute() {
            HashSet<HashEntry<K, V>> evictedCopy = new HashSet<HashEntry<K, V>>(this.evicted);
            for (HashEntry<K, V> e : this.accessQueue) {
                this.put(e, e.value);
            }
            this.accessQueue.clear();
            this.evicted.clear();
            return evictedCopy;
        }

        @Override
        public Set<HashEntry<K, V>> onEntryMiss(HashEntry<K, V> e) {
            this.put(e, e.value);
            if (!this.evicted.isEmpty()) {
                HashSet<HashEntry<K, V>> evictedCopy = new HashSet<HashEntry<K, V>>(this.evicted);
                this.evicted.clear();
                return evictedCopy;
            }
            return Collections.emptySet();
        }

        @Override
        public boolean onEntryHit(HashEntry<K, V> e) {
            this.accessQueue.add(e);
            return (float)this.accessQueue.size() >= (float)this.maxBatchQueueSize * this.batchThresholdFactor;
        }

        @Override
        public boolean thresholdExpired() {
            return this.accessQueue.size() >= this.maxBatchQueueSize;
        }

        @Override
        public void onEntryRemove(HashEntry<K, V> e) {
            this.remove(e);
            while (this.accessQueue.remove(e)) {
            }
        }

        @Override
        public void clear() {
            super.clear();
            this.accessQueue.clear();
        }

        @Override
        public Eviction strategy() {
            return Eviction.LRU;
        }

        protected boolean isAboveThreshold() {
            return this.size() > this.trimDownSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<HashEntry<K, V>, V> eldest) {
            boolean aboveThreshold = this.isAboveThreshold();
            if (aboveThreshold) {
                HashEntry<K, V> evictedEntry = eldest.getKey();
                this.segment.evictionListener.onEntryChosenForEviction(evictedEntry.value);
                this.segment.remove(evictedEntry.key, evictedEntry.hash, null);
                this.evicted.add(evictedEntry);
            }
            return aboveThreshold;
        }

        @Override
        public HashEntry<K, V> createNewEntry(K key, int hash, HashEntry<K, V> next, V value) {
            return new HashEntry<K, V>(key, hash, next, value);
        }
    }

    static class NullEvictionPolicy<K, V>
    implements EvictionPolicy<K, V> {
        NullEvictionPolicy() {
        }

        @Override
        public void clear() {
        }

        @Override
        public Set<HashEntry<K, V>> execute() {
            return Collections.emptySet();
        }

        @Override
        public boolean onEntryHit(HashEntry<K, V> e) {
            return false;
        }

        @Override
        public Set<HashEntry<K, V>> onEntryMiss(HashEntry<K, V> e) {
            return Collections.emptySet();
        }

        @Override
        public void onEntryRemove(HashEntry<K, V> e) {
        }

        @Override
        public boolean thresholdExpired() {
            return false;
        }

        @Override
        public Eviction strategy() {
            return Eviction.NONE;
        }

        @Override
        public HashEntry<K, V> createNewEntry(K key, int hash, HashEntry<K, V> next, V value) {
            return new HashEntry<K, V>(key, hash, next, value);
        }
    }

    public static interface EvictionPolicy<K, V> {
        public static final int MAX_BATCH_SIZE = 64;

        public HashEntry<K, V> createNewEntry(K var1, int var2, HashEntry<K, V> var3, V var4);

        public Set<HashEntry<K, V>> execute();

        public Set<HashEntry<K, V>> onEntryMiss(HashEntry<K, V> var1);

        public boolean onEntryHit(HashEntry<K, V> var1);

        public void onEntryRemove(HashEntry<K, V> var1);

        public void clear();

        public Eviction strategy();

        public boolean thresholdExpired();
    }

    static final class NullEvictionListener<K, V>
    implements EvictionListener<K, V> {
        NullEvictionListener() {
        }

        @Override
        public void onEntryEviction(Map<K, V> evicted) {
        }

        @Override
        public void onEntryChosenForEviction(V internalCacheEntry) {
        }
    }

    public static interface EvictionListener<K, V> {
        public void onEntryEviction(Map<K, V> var1);

        public void onEntryChosenForEviction(V var1);
    }

    public static enum Eviction {
        NONE{

            @Override
            public <K, V> EvictionPolicy<K, V> make(Segment<K, V> s, int capacity, float lf) {
                return new NullEvictionPolicy();
            }
        }
        ,
        LRU{

            @Override
            public <K, V> EvictionPolicy<K, V> make(Segment<K, V> s, int capacity, float lf) {
                return new LRU<K, V>(s, capacity, lf, capacity * 10, lf);
            }
        }
        ,
        LIRS{

            @Override
            public <K, V> EvictionPolicy<K, V> make(Segment<K, V> s, int capacity, float lf) {
                return new LIRS<K, V>(s, capacity, capacity * 10, lf);
            }
        };


        abstract <K, V> EvictionPolicy<K, V> make(Segment<K, V> var1, int var2, float var3);
    }

    private static enum Recency {
        HIR_RESIDENT,
        LIR_RESIDENT,
        HIR_NONRESIDENT;

    }

    private static class HashEntry<K, V> {
        final K key;
        final int hash;
        volatile V value;
        final HashEntry<K, V> next;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
        }

        public int hashCode() {
            int result = 17;
            result = result * 31 + this.hash;
            result = result * 31 + this.key.hashCode();
            return result;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            HashEntry other = (HashEntry)o;
            return this.hash == other.hash && this.key.equals(other.key);
        }

        static <K, V> HashEntry<K, V>[] newArray(int i) {
            return new HashEntry[i];
        }
    }
}

