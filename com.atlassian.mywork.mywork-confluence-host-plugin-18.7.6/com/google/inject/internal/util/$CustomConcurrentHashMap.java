/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$AbstractMapEntry;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$Nullable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

final class $CustomConcurrentHashMap {
    private $CustomConcurrentHashMap() {
    }

    private static int rehash(int h) {
        h += h << 15 ^ 0xFFFFCD7D;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class SimpleInternalEntry<K, V> {
        final K key;
        final int hash;
        final SimpleInternalEntry<K, V> next;
        volatile V value;

        SimpleInternalEntry(K key, int hash, @$Nullable V value, SimpleInternalEntry<K, V> next) {
            this.key = key;
            this.hash = hash;
            this.value = value;
            this.next = next;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class SimpleStrategy<K, V>
    implements Strategy<K, V, SimpleInternalEntry<K, V>> {
        SimpleStrategy() {
        }

        @Override
        public SimpleInternalEntry<K, V> newEntry(K key, int hash, SimpleInternalEntry<K, V> next) {
            return new SimpleInternalEntry<K, Object>(key, hash, null, next);
        }

        @Override
        public SimpleInternalEntry<K, V> copyEntry(K key, SimpleInternalEntry<K, V> original, SimpleInternalEntry<K, V> next) {
            return new SimpleInternalEntry(key, original.hash, original.value, next);
        }

        @Override
        public void setValue(SimpleInternalEntry<K, V> entry, V value) {
            entry.value = value;
        }

        @Override
        public V getValue(SimpleInternalEntry<K, V> entry) {
            return entry.value;
        }

        @Override
        public boolean equalKeys(K a, Object b) {
            return a.equals(b);
        }

        @Override
        public boolean equalValues(V a, Object b) {
            return a.equals(b);
        }

        @Override
        public int hashKey(Object key) {
            return key.hashCode();
        }

        @Override
        public K getKey(SimpleInternalEntry<K, V> entry) {
            return entry.key;
        }

        @Override
        public SimpleInternalEntry<K, V> getNext(SimpleInternalEntry<K, V> entry) {
            return entry.next;
        }

        @Override
        public int getHash(SimpleInternalEntry<K, V> entry) {
            return entry.hash;
        }

        @Override
        public void setInternals(Internals<K, V, SimpleInternalEntry<K, V>> internals) {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ComputingImpl<K, V, E>
    extends Impl<K, V, E> {
        static final long serialVersionUID = 0L;
        final ComputingStrategy<K, V, E> computingStrategy;
        final $Function<? super K, ? extends V> computer;

        ComputingImpl(ComputingStrategy<K, V, E> strategy, Builder builder, $Function<? super K, ? extends V> computer) {
            super(strategy, builder);
            this.computingStrategy = strategy;
            this.computer = computer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V get(Object k) {
            Object key = k;
            if (key == null) {
                throw new NullPointerException("key");
            }
            int hash = this.hash(key);
            Impl.Segment segment = this.segmentFor(hash);
            block12: while (true) {
                Object entry;
                if ((entry = segment.getEntry(key, hash)) == null) {
                    boolean created = false;
                    segment.lock();
                    try {
                        entry = segment.getEntry(key, hash);
                        if (entry == null) {
                            created = true;
                            int count = segment.count;
                            if (count++ > segment.threshold) {
                                segment.expand();
                            }
                            AtomicReferenceArray table = segment.table;
                            int index = hash & table.length() - 1;
                            Object first = table.get(index);
                            ++segment.modCount;
                            entry = this.computingStrategy.newEntry(key, hash, first);
                            table.set(index, entry);
                            segment.count = count;
                        }
                    }
                    finally {
                        segment.unlock();
                    }
                    if (created) {
                        boolean success = false;
                        try {
                            V value = this.computingStrategy.compute((K)key, entry, this.computer);
                            if (value == null) {
                                throw new NullPointerException("compute() returned null unexpectedly");
                            }
                            success = true;
                            V v = value;
                            return v;
                        }
                        finally {
                            if (!success) {
                                segment.removeEntry(entry, hash);
                            }
                        }
                    }
                }
                boolean interrupted = false;
                while (true) {
                    try {
                        V value = this.computingStrategy.waitForValue(entry);
                        if (value == null) {
                            segment.removeEntry(entry, hash);
                            continue block12;
                        }
                        V v = value;
                        return v;
                    }
                    catch (InterruptedException e) {
                        interrupted = true;
                        continue;
                    }
                    break;
                }
                finally {
                    if (!interrupted) continue;
                    Thread.currentThread().interrupt();
                    continue;
                }
                break;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Impl<K, V, E>
    extends AbstractMap<K, V>
    implements ConcurrentMap<K, V>,
    Serializable {
        static final int MAXIMUM_CAPACITY = 0x40000000;
        static final int MAX_SEGMENTS = 65536;
        static final int RETRIES_BEFORE_LOCK = 2;
        final Strategy<K, V, E> strategy;
        final int segmentMask;
        final int segmentShift;
        final Segment[] segments;
        final float loadFactor;
        Set<K> keySet;
        Collection<V> values;
        Set<Map.Entry<K, V>> entrySet;
        private static final long serialVersionUID = 0L;

        Impl(Strategy<K, V, E> strategy, Builder builder) {
            int segmentSize;
            int segmentCapacity;
            int segmentCount;
            this.loadFactor = builder.loadFactor;
            int concurrencyLevel = builder.concurrencyLevel;
            int initialCapacity = builder.initialCapacity;
            if (concurrencyLevel > 65536) {
                concurrencyLevel = 65536;
            }
            int segmentShift = 0;
            for (segmentCount = 1; segmentCount < concurrencyLevel; segmentCount <<= 1) {
                ++segmentShift;
            }
            this.segmentShift = 32 - segmentShift;
            this.segmentMask = segmentCount - 1;
            this.segments = this.newSegmentArray(segmentCount);
            if (initialCapacity > 0x40000000) {
                initialCapacity = 0x40000000;
            }
            if ((segmentCapacity = initialCapacity / segmentCount) * segmentCount < initialCapacity) {
                ++segmentCapacity;
            }
            for (segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {
            }
            for (int i = 0; i < this.segments.length; ++i) {
                this.segments[i] = new Segment(segmentSize);
            }
            this.strategy = strategy;
            strategy.setInternals(new InternalsImpl());
        }

        int hash(Object key) {
            int h = this.strategy.hashKey(key);
            return $CustomConcurrentHashMap.rehash(h);
        }

        Segment[] newSegmentArray(int ssize) {
            return (Segment[])Array.newInstance(Segment.class, ssize);
        }

        Segment segmentFor(int hash) {
            return this.segments[hash >>> this.segmentShift & this.segmentMask];
        }

        @Override
        public boolean isEmpty() {
            int i;
            Segment[] segments = this.segments;
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
            Segment[] segments = this.segments;
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
                sum = 0L;
                for (Segment segment : segments) {
                    segment.lock();
                }
                for (Segment segment : segments) {
                    sum += (long)segment.count;
                }
                for (Segment segment : segments) {
                    segment.unlock();
                }
            }
            if (sum > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            return (int)sum;
        }

        @Override
        public V get(Object key) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).get(key, hash);
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).containsKey(key, hash);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(Object value) {
            if (value == null) {
                throw new NullPointerException("value");
            }
            Segment[] segments = this.segments;
            int[] mc = new int[segments.length];
            for (int k = 0; k < 2; ++k) {
                int mcsum = 0;
                for (int i = 0; i < segments.length; ++i) {
                    int c = segments[i].count;
                    mc[i] = segments[i].modCount;
                    mcsum += mc[i];
                    if (!segments[i].containsValue(value)) continue;
                    return true;
                }
                boolean cleanSweep = true;
                if (mcsum != 0) {
                    for (int i = 0; i < segments.length; ++i) {
                        int c = segments[i].count;
                        if (mc[i] == segments[i].modCount) continue;
                        cleanSweep = false;
                        break;
                    }
                }
                if (!cleanSweep) continue;
                return false;
            }
            for (Segment segment : segments) {
                segment.lock();
            }
            boolean found = false;
            try {
                for (Segment segment : segments) {
                    if (!segment.containsValue(value)) continue;
                    found = true;
                    break;
                }
            }
            finally {
                for (Segment segment : segments) {
                    segment.unlock();
                }
            }
            return found;
        }

        @Override
        public V put(K key, V value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).put(key, hash, value, false);
        }

        @Override
        public V putIfAbsent(K key, V value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            int hash = this.hash(key);
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
            if (key == null) {
                throw new NullPointerException("key");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).remove(key, hash);
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).remove(key, hash, value);
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (oldValue == null) {
                throw new NullPointerException("oldValue");
            }
            if (newValue == null) {
                throw new NullPointerException("newValue");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
        }

        @Override
        public V replace(K key, V value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            int hash = this.hash(key);
            return this.segmentFor(hash).replace(key, hash, value);
        }

        @Override
        public void clear() {
            for (Segment segment : this.segments) {
                segment.clear();
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

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(this.size());
            out.writeFloat(this.loadFactor);
            out.writeInt(this.segments.length);
            out.writeObject(this.strategy);
            for (Map.Entry<K, V> entry : this.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }
            out.writeObject(null);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                Object key;
                int segmentSize;
                int segmentCapacity;
                int segmentCount;
                int initialCapacity = in.readInt();
                float loadFactor = in.readFloat();
                int concurrencyLevel = in.readInt();
                Strategy strategy = (Strategy)in.readObject();
                Fields.loadFactor.set(this, Float.valueOf(loadFactor));
                if (concurrencyLevel > 65536) {
                    concurrencyLevel = 65536;
                }
                int segmentShift = 0;
                for (segmentCount = 1; segmentCount < concurrencyLevel; segmentCount <<= 1) {
                    ++segmentShift;
                }
                Fields.segmentShift.set(this, 32 - segmentShift);
                Fields.segmentMask.set(this, segmentCount - 1);
                Fields.segments.set(this, this.newSegmentArray(segmentCount));
                if (initialCapacity > 0x40000000) {
                    initialCapacity = 0x40000000;
                }
                if ((segmentCapacity = initialCapacity / segmentCount) * segmentCount < initialCapacity) {
                    ++segmentCapacity;
                }
                for (segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {
                }
                for (int i = 0; i < this.segments.length; ++i) {
                    this.segments[i] = new Segment(segmentSize);
                }
                Fields.strategy.set(this, strategy);
                while ((key = in.readObject()) != null) {
                    Object value = in.readObject();
                    this.put(key, value);
                }
            }
            catch (IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
        }

        static class Fields {
            static final Field loadFactor = Fields.findField("loadFactor");
            static final Field segmentShift = Fields.findField("segmentShift");
            static final Field segmentMask = Fields.findField("segmentMask");
            static final Field segments = Fields.findField("segments");
            static final Field strategy = Fields.findField("strategy");

            Fields() {
            }

            static Field findField(String name) {
                try {
                    Field f = Impl.class.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                }
                catch (NoSuchFieldException e) {
                    throw new AssertionError((Object)e);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
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
                Object key = e.getKey();
                if (key == null) {
                    return false;
                }
                Object v = Impl.this.get(key);
                return v != null && Impl.this.strategy.equalValues(v, e.getValue());
            }

            @Override
            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry e = (Map.Entry)o;
                Object key = e.getKey();
                return key != null && Impl.this.remove(key, e.getValue());
            }

            @Override
            public int size() {
                return Impl.this.size();
            }

            @Override
            public boolean isEmpty() {
                return Impl.this.isEmpty();
            }

            @Override
            public void clear() {
                Impl.this.clear();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
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
                return Impl.this.size();
            }

            @Override
            public boolean isEmpty() {
                return Impl.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return Impl.this.containsValue(o);
            }

            @Override
            public void clear() {
                Impl.this.clear();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
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
                return Impl.this.size();
            }

            @Override
            public boolean isEmpty() {
                return Impl.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return Impl.this.containsKey(o);
            }

            @Override
            public boolean remove(Object o) {
                return Impl.this.remove(o) != null;
            }

            @Override
            public void clear() {
                Impl.this.clear();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        final class EntryIterator
        extends HashIterator
        implements Iterator<Map.Entry<K, V>> {
            EntryIterator() {
            }

            @Override
            public Map.Entry<K, V> next() {
                return this.nextEntry();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        final class WriteThroughEntry
        extends $AbstractMapEntry<K, V> {
            final K key;
            V value;

            WriteThroughEntry(K key, V value) {
                this.key = key;
                this.value = value;
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
                if (value == null) {
                    throw new NullPointerException();
                }
                Object oldValue = Impl.this.put(this.getKey(), value);
                this.value = value;
                return oldValue;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        final class ValueIterator
        extends HashIterator
        implements Iterator<V> {
            ValueIterator() {
            }

            @Override
            public V next() {
                return super.nextEntry().getValue();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        final class KeyIterator
        extends HashIterator
        implements Iterator<K> {
            KeyIterator() {
            }

            @Override
            public K next() {
                return super.nextEntry().getKey();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        abstract class HashIterator {
            int nextSegmentIndex;
            int nextTableIndex;
            AtomicReferenceArray<E> currentTable;
            E nextEntry;
            WriteThroughEntry nextExternal;
            WriteThroughEntry lastReturned;

            HashIterator() {
                this.nextSegmentIndex = Impl.this.segments.length - 1;
                this.nextTableIndex = -1;
                this.advance();
            }

            public boolean hasMoreElements() {
                return this.hasNext();
            }

            final void advance() {
                this.nextExternal = null;
                if (this.nextInChain()) {
                    return;
                }
                if (this.nextInTable()) {
                    return;
                }
                while (this.nextSegmentIndex >= 0) {
                    Segment seg = Impl.this.segments[this.nextSegmentIndex--];
                    if (seg.count == 0) continue;
                    this.currentTable = seg.table;
                    this.nextTableIndex = this.currentTable.length() - 1;
                    if (!this.nextInTable()) continue;
                    return;
                }
            }

            boolean nextInChain() {
                Strategy s = Impl.this.strategy;
                if (this.nextEntry != null) {
                    this.nextEntry = s.getNext(this.nextEntry);
                    while (this.nextEntry != null) {
                        if (this.advanceTo(this.nextEntry)) {
                            return true;
                        }
                        this.nextEntry = s.getNext(this.nextEntry);
                    }
                }
                return false;
            }

            boolean nextInTable() {
                while (this.nextTableIndex >= 0) {
                    if ((this.nextEntry = this.currentTable.get(this.nextTableIndex--)) == null || !this.advanceTo(this.nextEntry) && !this.nextInChain()) continue;
                    return true;
                }
                return false;
            }

            boolean advanceTo(E entry) {
                Strategy s = Impl.this.strategy;
                Object key = s.getKey(entry);
                Object value = s.getValue(entry);
                if (key != null && value != null) {
                    this.nextExternal = new WriteThroughEntry(key, value);
                    return true;
                }
                return false;
            }

            public boolean hasNext() {
                return this.nextExternal != null;
            }

            WriteThroughEntry nextEntry() {
                if (this.nextExternal == null) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.nextExternal;
                this.advance();
                return this.lastReturned;
            }

            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                Impl.this.remove(this.lastReturned.getKey());
                this.lastReturned = null;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        final class Segment
        extends ReentrantLock {
            volatile int count;
            int modCount;
            int threshold;
            volatile AtomicReferenceArray<E> table;

            Segment(int initialCapacity) {
                this.setTable(this.newEntryArray(initialCapacity));
            }

            AtomicReferenceArray<E> newEntryArray(int size) {
                return new AtomicReferenceArray(size);
            }

            void setTable(AtomicReferenceArray<E> newTable) {
                this.threshold = (int)((float)newTable.length() * Impl.this.loadFactor);
                this.table = newTable;
            }

            E getFirst(int hash) {
                AtomicReferenceArray table = this.table;
                return table.get(hash & table.length() - 1);
            }

            public E getEntry(Object key, int hash) {
                Strategy s = Impl.this.strategy;
                if (this.count != 0) {
                    Object e = this.getFirst(hash);
                    while (e != null) {
                        Object entryKey;
                        if (s.getHash(e) == hash && (entryKey = s.getKey(e)) != null && s.equalKeys(entryKey, key)) {
                            return e;
                        }
                        e = s.getNext(e);
                    }
                }
                return null;
            }

            V get(Object key, int hash) {
                Object entry = this.getEntry(key, hash);
                if (entry == null) {
                    return null;
                }
                return Impl.this.strategy.getValue(entry);
            }

            boolean containsKey(Object key, int hash) {
                Strategy s = Impl.this.strategy;
                if (this.count != 0) {
                    Object e = this.getFirst(hash);
                    while (e != null) {
                        Object entryKey;
                        if (s.getHash(e) == hash && (entryKey = s.getKey(e)) != null && s.equalKeys(entryKey, key)) {
                            return s.getValue(e) != null;
                        }
                        e = s.getNext(e);
                    }
                }
                return false;
            }

            boolean containsValue(Object value) {
                Strategy s = Impl.this.strategy;
                if (this.count != 0) {
                    AtomicReferenceArray table = this.table;
                    int length = table.length();
                    for (int i = 0; i < length; ++i) {
                        Object e = table.get(i);
                        while (e != null) {
                            Object entryValue = s.getValue(e);
                            if (entryValue != null && s.equalValues(entryValue, value)) {
                                return true;
                            }
                            e = s.getNext(e);
                        }
                    }
                }
                return false;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            boolean replace(K key, int hash, V oldValue, V newValue) {
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object e = this.getFirst(hash);
                    while (e != null) {
                        Object entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(key, entryKey)) {
                            Object entryValue = s.getValue(e);
                            if (entryValue == null) {
                                boolean bl = false;
                                return bl;
                            }
                            if (s.equalValues(entryValue, oldValue)) {
                                s.setValue(e, newValue);
                                boolean bl = true;
                                return bl;
                            }
                        }
                        e = s.getNext(e);
                    }
                    boolean bl = false;
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
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object e = this.getFirst(hash);
                    while (e != null) {
                        Object entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(key, entryKey)) {
                            Object entryValue = s.getValue(e);
                            if (entryValue == null) {
                                Object v = null;
                                return v;
                            }
                            s.setValue(e, newValue);
                            Object v = entryValue;
                            return v;
                        }
                        e = s.getNext(e);
                    }
                    Object v = null;
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
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object first;
                    int count = this.count;
                    if (count++ > this.threshold) {
                        this.expand();
                    }
                    AtomicReferenceArray table = this.table;
                    int index = hash & table.length() - 1;
                    Object e = first = table.get(index);
                    while (e != null) {
                        Object entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(key, entryKey)) {
                            Object entryValue = s.getValue(e);
                            if (onlyIfAbsent && entryValue != null) {
                                Object v = entryValue;
                                return v;
                            }
                            s.setValue(e, value);
                            Object v = entryValue;
                            return v;
                        }
                        e = s.getNext(e);
                    }
                    ++this.modCount;
                    Object newEntry = s.newEntry(key, hash, first);
                    s.setValue(newEntry, value);
                    table.set(index, newEntry);
                    this.count = count;
                    Object v = null;
                    return v;
                }
                finally {
                    this.unlock();
                }
            }

            void expand() {
                AtomicReferenceArray oldTable = this.table;
                int oldCapacity = oldTable.length();
                if (oldCapacity >= 0x40000000) {
                    return;
                }
                Strategy s = Impl.this.strategy;
                AtomicReferenceArray newTable = this.newEntryArray(oldCapacity << 1);
                this.threshold = (int)((float)newTable.length() * Impl.this.loadFactor);
                int newMask = newTable.length() - 1;
                for (int oldIndex = 0; oldIndex < oldCapacity; ++oldIndex) {
                    Object head = oldTable.get(oldIndex);
                    if (head == null) continue;
                    Object next = s.getNext(head);
                    int headIndex = s.getHash(head) & newMask;
                    if (next == null) {
                        newTable.set(headIndex, head);
                        continue;
                    }
                    Object tail = head;
                    int tailIndex = headIndex;
                    Object last = next;
                    while (last != null) {
                        int newIndex = s.getHash(last) & newMask;
                        if (newIndex != tailIndex) {
                            tailIndex = newIndex;
                            tail = last;
                        }
                        last = s.getNext(last);
                    }
                    newTable.set(tailIndex, tail);
                    Object e = head;
                    while (e != tail) {
                        Object key = s.getKey(e);
                        if (key != null) {
                            int newIndex = s.getHash(e) & newMask;
                            Object newNext = newTable.get(newIndex);
                            newTable.set(newIndex, s.copyEntry(key, e, newNext));
                        }
                        e = s.getNext(e);
                    }
                }
                this.table = newTable;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            V remove(Object key, int hash) {
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object first;
                    int count = this.count - 1;
                    AtomicReferenceArray table = this.table;
                    int index = hash & table.length() - 1;
                    Object e = first = table.get(index);
                    while (e != null) {
                        Object entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(entryKey, key)) {
                            Object entryValue = Impl.this.strategy.getValue(e);
                            ++this.modCount;
                            Object newFirst = s.getNext(e);
                            Object p = first;
                            while (p != e) {
                                Object pKey = s.getKey(p);
                                if (pKey != null) {
                                    newFirst = s.copyEntry(pKey, p, newFirst);
                                }
                                p = s.getNext(p);
                            }
                            table.set(index, newFirst);
                            this.count = count;
                            Object v = entryValue;
                            return v;
                        }
                        e = s.getNext(e);
                    }
                    Object v = null;
                    return v;
                }
                finally {
                    this.unlock();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            boolean remove(Object key, int hash, Object value) {
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object first;
                    int count = this.count - 1;
                    AtomicReferenceArray table = this.table;
                    int index = hash & table.length() - 1;
                    Object e = first = table.get(index);
                    while (e != null) {
                        Object entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(entryKey, key)) {
                            Object entryValue = Impl.this.strategy.getValue(e);
                            if (value == entryValue || value != null && entryValue != null && s.equalValues(entryValue, value)) {
                                ++this.modCount;
                                Object newFirst = s.getNext(e);
                                Object p = first;
                                while (p != e) {
                                    Object pKey = s.getKey(p);
                                    if (pKey != null) {
                                        newFirst = s.copyEntry(pKey, p, newFirst);
                                    }
                                    p = s.getNext(p);
                                }
                                table.set(index, newFirst);
                                this.count = count;
                                boolean bl = true;
                                return bl;
                            }
                            boolean bl = false;
                            return bl;
                        }
                        e = s.getNext(e);
                    }
                    boolean bl = false;
                    return bl;
                }
                finally {
                    this.unlock();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public boolean removeEntry(E entry, int hash, V value) {
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object first;
                    int count = this.count - 1;
                    AtomicReferenceArray table = this.table;
                    int index = hash & table.length() - 1;
                    Object e = first = table.get(index);
                    while (e != null) {
                        if (s.getHash(e) == hash && entry.equals(e)) {
                            Object entryValue = s.getValue(e);
                            if (entryValue == value || value != null && s.equalValues(entryValue, value)) {
                                ++this.modCount;
                                Object newFirst = s.getNext(e);
                                Object p = first;
                                while (p != e) {
                                    Object pKey = s.getKey(p);
                                    if (pKey != null) {
                                        newFirst = s.copyEntry(pKey, p, newFirst);
                                    }
                                    p = s.getNext(p);
                                }
                                table.set(index, newFirst);
                                this.count = count;
                                boolean bl = true;
                                return bl;
                            }
                            boolean bl = false;
                            return bl;
                        }
                        e = s.getNext(e);
                    }
                    boolean bl = false;
                    return bl;
                }
                finally {
                    this.unlock();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public boolean removeEntry(E entry, int hash) {
                Strategy s = Impl.this.strategy;
                this.lock();
                try {
                    Object first;
                    int count = this.count - 1;
                    AtomicReferenceArray table = this.table;
                    int index = hash & table.length() - 1;
                    Object e = first = table.get(index);
                    while (e != null) {
                        if (s.getHash(e) == hash && entry.equals(e)) {
                            ++this.modCount;
                            Object newFirst = s.getNext(e);
                            Object p = first;
                            while (p != e) {
                                Object pKey = s.getKey(p);
                                if (pKey != null) {
                                    newFirst = s.copyEntry(pKey, p, newFirst);
                                }
                                p = s.getNext(p);
                            }
                            table.set(index, newFirst);
                            this.count = count;
                            boolean bl = true;
                            return bl;
                        }
                        e = s.getNext(e);
                    }
                    boolean bl = false;
                    return bl;
                }
                finally {
                    this.unlock();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            void clear() {
                if (this.count != 0) {
                    this.lock();
                    try {
                        AtomicReferenceArray table = this.table;
                        for (int i = 0; i < table.length(); ++i) {
                            table.set(i, null);
                        }
                        ++this.modCount;
                        this.count = 0;
                    }
                    finally {
                        this.unlock();
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        class InternalsImpl
        implements Internals<K, V, E>,
        Serializable {
            static final long serialVersionUID = 0L;

            InternalsImpl() {
            }

            @Override
            public E getEntry(K key) {
                if (key == null) {
                    throw new NullPointerException("key");
                }
                int hash = Impl.this.hash(key);
                return Impl.this.segmentFor(hash).getEntry(key, hash);
            }

            @Override
            public boolean removeEntry(E entry, V value) {
                if (entry == null) {
                    throw new NullPointerException("entry");
                }
                int hash = Impl.this.strategy.getHash(entry);
                return Impl.this.segmentFor(hash).removeEntry(entry, hash, value);
            }

            @Override
            public boolean removeEntry(E entry) {
                if (entry == null) {
                    throw new NullPointerException("entry");
                }
                int hash = Impl.this.strategy.getHash(entry);
                return Impl.this.segmentFor(hash).removeEntry(entry, hash);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface ComputingStrategy<K, V, E>
    extends Strategy<K, V, E> {
        public V compute(K var1, E var2, $Function<? super K, ? extends V> var3);

        public V waitForValue(E var1) throws InterruptedException;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Internals<K, V, E> {
        public E getEntry(K var1);

        public boolean removeEntry(E var1, @$Nullable V var2);

        public boolean removeEntry(E var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Strategy<K, V, E> {
        public E newEntry(K var1, int var2, E var3);

        public E copyEntry(K var1, E var2, E var3);

        public void setValue(E var1, V var2);

        public V getValue(E var1);

        public boolean equalKeys(K var1, Object var2);

        public boolean equalValues(V var1, Object var2);

        public int hashKey(Object var1);

        public K getKey(E var1);

        public E getNext(E var1);

        public int getHash(E var1);

        public void setInternals(Internals<K, V, E> var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class Builder {
        float loadFactor = 0.75f;
        int initialCapacity = 16;
        int concurrencyLevel = 16;

        Builder() {
        }

        public Builder loadFactor(float loadFactor) {
            if (loadFactor <= 0.0f) {
                throw new IllegalArgumentException();
            }
            this.loadFactor = loadFactor;
            return this;
        }

        public Builder initialCapacity(int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException();
            }
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder concurrencyLevel(int concurrencyLevel) {
            if (concurrencyLevel <= 0) {
                throw new IllegalArgumentException();
            }
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        public <K, V, E> ConcurrentMap<K, V> buildMap(Strategy<K, V, E> strategy) {
            if (strategy == null) {
                throw new NullPointerException("strategy");
            }
            return new Impl<K, V, E>(strategy, this);
        }

        public <K, V, E> ConcurrentMap<K, V> buildComputingMap(ComputingStrategy<K, V, E> strategy, $Function<? super K, ? extends V> computer) {
            if (strategy == null) {
                throw new NullPointerException("strategy");
            }
            if (computer == null) {
                throw new NullPointerException("computer");
            }
            return new ComputingImpl<K, V, E>(strategy, this, computer);
        }
    }
}

