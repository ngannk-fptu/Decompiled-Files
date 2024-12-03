/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentReferenceHashMap<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    static final ReferenceType DEFAULT_KEY_TYPE = ReferenceType.WEAK;
    static final ReferenceType DEFAULT_VALUE_TYPE = ReferenceType.STRONG;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int MAXIMUM_CAPACITY = 0x40000000;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    boolean identityComparisons;
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

    private int hashOf(Object key) {
        return ConcurrentReferenceHashMap.hash(this.identityComparisons ? System.identityHashCode(key) : key.hashCode());
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType keyType, ReferenceType valueType, EnumSet<Option> options) {
        int cap;
        int c;
        int ssize;
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0) {
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
        if (initialCapacity > 0x40000000) {
            initialCapacity = 0x40000000;
        }
        if ((c = initialCapacity / ssize) * ssize < initialCapacity) {
            ++c;
        }
        for (cap = 1; cap < c; cap <<= 1) {
        }
        this.identityComparisons = options != null && options.contains((Object)Option.IDENTITY_COMPARISONS);
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(cap, loadFactor, keyType, valueType, this.identityComparisons);
        }
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this(initialCapacity, loadFactor, concurrencyLevel, DEFAULT_KEY_TYPE, DEFAULT_VALUE_TYPE, null);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 16);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, ReferenceType keyType, ReferenceType valueType) {
        this(initialCapacity, 0.75f, 16, keyType, valueType, null);
    }

    public ConcurrentReferenceHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f, 16);
    }

    public ConcurrentReferenceHashMap() {
        this(16, 0.75f, 16);
    }

    public ConcurrentReferenceHashMap(Map<? extends K, ? extends V> m) {
        this(Math.max((int)((float)m.size() / 0.75f) + 1, 16), 0.75f, 16);
        this.putAll(m);
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
            for (i = 0; i < segments.length; ++i) {
                sum += (long)segments[i].count;
            }
            for (i = 0; i < segments.length; ++i) {
                segments[i].unlock();
            }
        }
        if (sum > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }

    @Override
    public V get(Object key) {
        int hash = this.hashOf(key);
        return this.segmentFor(hash).get(key, hash);
    }

    @Override
    public boolean containsKey(Object key) {
        int hash = this.hashOf(key);
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
                mc[i2] = segments[i2].modCount;
                mcsum += mc[i2];
                if (!segments[i2].containsValue(value)) continue;
                return true;
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int i3 = 0; i3 < segments.length; ++i3) {
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
        int hash = this.hashOf(key);
        return this.segmentFor(hash).put(key, hash, value, false);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = this.hashOf(key);
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
        int hash = this.hashOf(key);
        return this.segmentFor(hash).remove(key, hash, null, false);
    }

    @Override
    public boolean remove(Object key, Object value) {
        int hash = this.hashOf(key);
        if (value == null) {
            return false;
        }
        return this.segmentFor(hash).remove(key, hash, value, false) != null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        int hash = this.hashOf(key);
        return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = this.hashOf(key);
        return this.segmentFor(hash).replace(key, hash, value);
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].clear();
        }
    }

    public void purgeStaleEntries() {
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].removeStale();
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
                        Object key = e.key();
                        if (key != null) {
                            s.writeObject(key);
                            s.writeObject(e.value());
                        }
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
            Object v = ConcurrentReferenceHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return ConcurrentReferenceHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
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
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return ConcurrentReferenceHashMap.this.containsValue(o);
        }

        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
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
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return ConcurrentReferenceHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return ConcurrentReferenceHashMap.this.remove(o) != null;
        }

        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
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
            return new WriteThroughEntry(e.key(), e.value());
        }
    }

    final class WriteThroughEntry
    extends SimpleEntry<K, V> {
        private static final long serialVersionUID = -7900634345345313646L;

        WriteThroughEntry(K k, V v) {
            super(k, v);
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            Object v = super.setValue(value);
            ConcurrentReferenceHashMap.this.put(this.getKey(), value);
            return v;
        }
    }

    static class SimpleEntry<K, V>
    implements Map.Entry<K, V>,
    Serializable {
        private static final long serialVersionUID = -8499721149061103585L;
        private final K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(Map.Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return SimpleEntry.eq(this.key, e.getKey()) && SimpleEntry.eq(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        private static boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
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
            return super.nextEntry().value();
        }

        @Override
        public V nextElement() {
            return super.nextEntry().value();
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
            return super.nextEntry().key();
        }

        @Override
        public K nextElement() {
            return super.nextEntry().key();
        }
    }

    abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;
        K currentKey;

        HashIterator() {
            this.nextSegmentIndex = ConcurrentReferenceHashMap.this.segments.length - 1;
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
                Segment seg = ConcurrentReferenceHashMap.this.segments[this.nextSegmentIndex--];
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
            while (this.nextEntry != null) {
                if (this.nextEntry.key() != null) {
                    return true;
                }
                this.advance();
            }
            return false;
        }

        HashEntry<K, V> nextEntry() {
            do {
                if (this.nextEntry == null) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.nextEntry;
                this.currentKey = this.lastReturned.key();
                this.advance();
            } while (this.currentKey == null);
            return this.lastReturned;
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentReferenceHashMap.this.remove(this.currentKey);
            this.lastReturned = null;
        }
    }

    static final class Segment<K, V>
    extends ReentrantLock
    implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        volatile transient int count;
        transient int modCount;
        transient int threshold;
        volatile transient HashEntry<K, V>[] table;
        final float loadFactor;
        volatile transient ReferenceQueue<Object> refQueue;
        final ReferenceType keyType;
        final ReferenceType valueType;
        final boolean identityComparisons;

        Segment(int initialCapacity, float lf, ReferenceType keyType, ReferenceType valueType, boolean identityComparisons) {
            this.loadFactor = lf;
            this.keyType = keyType;
            this.valueType = valueType;
            this.identityComparisons = identityComparisons;
            this.setTable(HashEntry.newArray(initialCapacity));
        }

        static final <K, V> Segment<K, V>[] newArray(int i) {
            return new Segment[i];
        }

        private boolean keyEq(Object src, Object dest) {
            return this.identityComparisons ? src == dest : src.equals(dest);
        }

        void setTable(HashEntry<K, V>[] newTable) {
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            this.table = newTable;
            this.refQueue = new ReferenceQueue();
        }

        HashEntry<K, V> getFirst(int hash) {
            HashEntry<K, V>[] tab = this.table;
            return tab[hash & tab.length - 1];
        }

        HashEntry<K, V> newHashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            return new HashEntry<K, V>(key, hash, next, value, this.keyType, this.valueType, this.refQueue);
        }

        V readValueUnderLock(HashEntry<K, V> e) {
            this.lock();
            try {
                this.removeStale();
                V v = e.value();
                return v;
            }
            finally {
                this.unlock();
            }
        }

        V get(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V> e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && this.keyEq(key, e.key())) {
                        Object opaque = e.valueRef;
                        if (opaque != null) {
                            return e.dereferenceValue(opaque);
                        }
                        return this.readValueUnderLock(e);
                    }
                    e = e.next;
                }
            }
            return null;
        }

        boolean containsKey(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V> e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && this.keyEq(key, e.key())) {
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
                        Object opaque = e.valueRef;
                        V v = opaque == null ? this.readValueUnderLock(e) : e.dereferenceValue(opaque);
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
            try {
                this.removeStale();
                HashEntry<K, V> e = this.getFirst(hash);
                while (!(e == null || e.hash == hash && this.keyEq(key, e.key()))) {
                    e = e.next;
                }
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value())) {
                    replaced = true;
                    e.setValue(newValue, this.valueType, this.refQueue);
                }
                boolean bl = replaced;
                return bl;
            }
            finally {
                this.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        V replace(K key, int hash, V newValue) {
            this.lock();
            try {
                this.removeStale();
                HashEntry<K, V> e = this.getFirst(hash);
                while (!(e == null || e.hash == hash && this.keyEq(key, e.key()))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value();
                    e.setValue(newValue, this.valueType, this.refQueue);
                }
                V v = oldValue;
                return v;
            }
            finally {
                this.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            this.lock();
            try {
                V oldValue;
                HashEntry<K, V> first;
                int reduced;
                this.removeStale();
                int c = this.count;
                if (c++ > this.threshold && (reduced = this.rehash()) > 0) {
                    this.count = (c -= reduced) - 1;
                }
                HashEntry<K, V>[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry<K, V> e = first = tab[index];
                while (!(e == null || e.hash == hash && this.keyEq(key, e.key()))) {
                    e = e.next;
                }
                if (e != null) {
                    oldValue = e.value();
                    if (!onlyIfAbsent || oldValue == null) {
                        e.setValue(value, this.valueType, this.refQueue);
                    }
                } else {
                    oldValue = null;
                    ++this.modCount;
                    tab[index] = this.newHashEntry(key, hash, first, value);
                    this.count = c;
                }
                V v = oldValue;
                return v;
            }
            finally {
                this.unlock();
            }
        }

        int rehash() {
            HashEntry<K, V>[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= 0x40000000) {
                return 0;
            }
            HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            int sizeMask = newTable.length - 1;
            int reduce = 0;
            for (int i = 0; i < oldCapacity; ++i) {
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
                    int k = last.hash & sizeMask;
                    if (k != lastIdx) {
                        lastIdx = k;
                        lastRun = last;
                    }
                    last = last.next;
                }
                newTable[lastIdx] = lastRun;
                HashEntry<K, V> p = e;
                while (p != lastRun) {
                    K key = p.key();
                    if (key == null) {
                        ++reduce;
                    } else {
                        int k = p.hash & sizeMask;
                        HashEntry n = newTable[k];
                        newTable[k] = this.newHashEntry(key, p.hash, n, p.value());
                    }
                    p = p.next;
                }
            }
            this.table = newTable;
            return reduce;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        V remove(Object key, int hash, Object value, boolean refRemove) {
            this.lock();
            try {
                HashEntry<K, V> first;
                if (!refRemove) {
                    this.removeStale();
                }
                int c = this.count - 1;
                HashEntry<K, V>[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry<K, V> e = first = tab[index];
                while (e != null && key != e.keyRef && (refRemove || hash != e.hash || !this.keyEq(key, e.key()))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    V v = e.value();
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        ++this.modCount;
                        HashEntry newFirst = e.next;
                        HashEntry<K, V> p = first;
                        while (p != e) {
                            K pKey = p.key();
                            if (pKey == null) {
                                --c;
                            } else {
                                newFirst = this.newHashEntry(pKey, p.hash, newFirst, p.value());
                            }
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

        final void removeStale() {
            KeyReference ref;
            while ((ref = (KeyReference)((Object)this.refQueue.poll())) != null) {
                this.remove(ref.keyRef(), ref.keyHash(), null, true);
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
                    this.refQueue = new ReferenceQueue();
                    this.count = 0;
                }
                finally {
                    this.unlock();
                }
            }
        }
    }

    static final class HashEntry<K, V> {
        final Object keyRef;
        final int hash;
        volatile Object valueRef;
        final HashEntry<K, V> next;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value, ReferenceType keyType, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
            this.hash = hash;
            this.next = next;
            this.keyRef = this.newKeyReference(key, keyType, refQueue);
            this.valueRef = this.newValueReference(value, valueType, refQueue);
        }

        final Object newKeyReference(K key, ReferenceType keyType, ReferenceQueue<Object> refQueue) {
            if (keyType == ReferenceType.WEAK) {
                return new WeakKeyReference<K>(key, this.hash, refQueue);
            }
            if (keyType == ReferenceType.SOFT) {
                return new SoftKeyReference<K>(key, this.hash, refQueue);
            }
            return key;
        }

        final Object newValueReference(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
            if (valueType == ReferenceType.WEAK) {
                return new WeakValueReference<V>(value, this.keyRef, this.hash, refQueue);
            }
            if (valueType == ReferenceType.SOFT) {
                return new SoftValueReference<V>(value, this.keyRef, this.hash, refQueue);
            }
            return value;
        }

        final K key() {
            if (this.keyRef instanceof KeyReference) {
                return (K)((Reference)this.keyRef).get();
            }
            return (K)this.keyRef;
        }

        final V value() {
            return this.dereferenceValue(this.valueRef);
        }

        final V dereferenceValue(Object value) {
            if (value instanceof KeyReference) {
                return (V)((Reference)value).get();
            }
            return (V)value;
        }

        final void setValue(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
            this.valueRef = this.newValueReference(value, valueType, refQueue);
        }

        static final <K, V> HashEntry<K, V>[] newArray(int i) {
            return new HashEntry[i];
        }
    }

    static final class SoftValueReference<V>
    extends SoftReference<V>
    implements KeyReference {
        final Object keyRef;
        final int hash;

        SoftValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
            super(value, refQueue);
            this.keyRef = keyRef;
            this.hash = hash;
        }

        @Override
        public final int keyHash() {
            return this.hash;
        }

        @Override
        public final Object keyRef() {
            return this.keyRef;
        }
    }

    static final class WeakValueReference<V>
    extends WeakReference<V>
    implements KeyReference {
        final Object keyRef;
        final int hash;

        WeakValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
            super(value, refQueue);
            this.keyRef = keyRef;
            this.hash = hash;
        }

        @Override
        public final int keyHash() {
            return this.hash;
        }

        @Override
        public final Object keyRef() {
            return this.keyRef;
        }
    }

    static final class SoftKeyReference<K>
    extends SoftReference<K>
    implements KeyReference {
        final int hash;

        SoftKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.hash = hash;
        }

        @Override
        public final int keyHash() {
            return this.hash;
        }

        @Override
        public final Object keyRef() {
            return this;
        }
    }

    static final class WeakKeyReference<K>
    extends WeakReference<K>
    implements KeyReference {
        final int hash;

        WeakKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.hash = hash;
        }

        @Override
        public final int keyHash() {
            return this.hash;
        }

        @Override
        public final Object keyRef() {
            return this;
        }
    }

    static interface KeyReference {
        public int keyHash();

        public Object keyRef();
    }

    public static enum Option {
        IDENTITY_COMPARISONS;

    }

    public static enum ReferenceType {
        STRONG,
        WEAK,
        SOFT;

    }
}

