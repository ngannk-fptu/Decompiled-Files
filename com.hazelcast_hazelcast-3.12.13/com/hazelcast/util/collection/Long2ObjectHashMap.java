/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import com.hazelcast.util.collection.Hashing;
import com.hazelcast.util.function.LongFunction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Long2ObjectHashMap<V>
implements Map<Long, V> {
    public static final double DEFAULT_LOAD_FACTOR = 0.6;
    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    private final double loadFactor;
    private int resizeThreshold;
    private int capacity;
    private int mask;
    private int size;
    private long[] keys;
    private Object[] values;
    private final ValueCollection valueCollection = new ValueCollection();
    private final KeySet keySet = new KeySet();
    private final EntrySet entrySet = new EntrySet();

    public Long2ObjectHashMap() {
        this(8, 0.6);
    }

    public Long2ObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.6);
    }

    public Long2ObjectHashMap(int initialCapacity, double loadFactor) {
        this.loadFactor = loadFactor;
        this.capacity = QuickMath.nextPowerOfTwo(initialCapacity);
        this.mask = this.capacity - 1;
        this.resizeThreshold = (int)((double)this.capacity * loadFactor);
        this.keys = new long[this.capacity];
        this.values = new Object[this.capacity];
    }

    public double loadFactor() {
        return this.loadFactor;
    }

    public int capacity() {
        return this.capacity;
    }

    public int resizeThreshold() {
        return this.resizeThreshold;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return 0 == this.size;
    }

    @Override
    public boolean containsKey(Object key) {
        Preconditions.checkNotNull(key, "Null keys are not permitted");
        return this.containsKey((Long)key);
    }

    public boolean containsKey(long key) {
        int index = Hashing.longHash(key, this.mask);
        while (null != this.values[index]) {
            if (key == this.keys[index]) {
                return true;
            }
            ++index;
            index &= this.mask;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        Preconditions.checkNotNull(value, "Null values are not permitted");
        for (Object v : this.values) {
            if (null == v || !value.equals(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        return this.get((Long)key);
    }

    public V get(long key) {
        Object value;
        int index = Hashing.longHash(key, this.mask);
        while (null != (value = this.values[index])) {
            if (key == this.keys[index]) {
                return (V)value;
            }
            ++index;
            index &= this.mask;
        }
        return null;
    }

    public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
        Preconditions.checkNotNull(mappingFunction, "mappingFunction cannot be null");
        V value = this.get(key);
        if (value == null && (value = mappingFunction.apply(key)) != null) {
            this.put(key, value);
        }
        return value;
    }

    @Override
    public V put(Long key, V value) {
        return this.put((long)key, value);
    }

    @Override
    public V put(long key, V value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        Object oldValue = null;
        int index = Hashing.longHash(key, this.mask);
        while (null != this.values[index]) {
            if (key == this.keys[index]) {
                oldValue = this.values[index];
                break;
            }
            ++index;
            index &= this.mask;
        }
        if (null == oldValue) {
            ++this.size;
            this.keys[index] = key;
        }
        this.values[index] = value;
        if (this.size > this.resizeThreshold) {
            this.increaseCapacity();
        }
        return (V)oldValue;
    }

    @Override
    public V remove(Object key) {
        return this.remove((Long)key);
    }

    public V remove(long key) {
        Object value;
        int index = Hashing.longHash(key, this.mask);
        while (null != (value = this.values[index])) {
            if (key == this.keys[index]) {
                this.values[index] = null;
                --this.size;
                this.compactChain(index);
                return (V)value;
            }
            ++index;
            index &= this.mask;
        }
        return null;
    }

    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill(this.values, null);
    }

    public void compact() {
        int idealCapacity = (int)Math.round((double)this.size() * (1.0 / this.loadFactor));
        this.rehash(QuickMath.nextPowerOfTwo(idealCapacity));
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> map) {
        for (Map.Entry<Long, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public KeySet keySet() {
        return this.keySet;
    }

    @Override
    public Collection<V> values() {
        return this.valueCollection;
    }

    @Override
    public Set<Map.Entry<Long, V>> entrySet() {
        return this.entrySet;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (Map.Entry<Long, V> entry : this.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append(", ");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append('}');
        return sb.toString();
    }

    private void increaseCapacity() {
        int newCapacity = this.capacity << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Max capacity reached at size=" + this.size);
        }
        this.rehash(newCapacity);
    }

    private void rehash(int newCapacity) {
        if (1 != Integer.bitCount(newCapacity)) {
            throw new IllegalStateException("New capacity must be a power of two");
        }
        this.capacity = newCapacity;
        this.mask = newCapacity - 1;
        this.resizeThreshold = (int)((double)newCapacity * this.loadFactor);
        long[] tempKeys = new long[this.capacity];
        Object[] tempValues = new Object[this.capacity];
        int size = this.values.length;
        for (int i = 0; i < size; ++i) {
            Object value = this.values[i];
            if (null == value) continue;
            long key = this.keys[i];
            int newHash = Hashing.longHash(key, this.mask);
            while (null != tempValues[newHash]) {
                ++newHash;
                newHash &= this.mask;
            }
            tempKeys[newHash] = key;
            tempValues[newHash] = value;
        }
        this.keys = tempKeys;
        this.values = tempValues;
    }

    private void compactChain(int deleteIndex) {
        int index = deleteIndex;
        while (true) {
            ++index;
            if (null == this.values[index &= this.mask]) {
                return;
            }
            int hash = Hashing.longHash(this.keys[index], this.mask);
            if ((index >= hash || hash > deleteIndex && deleteIndex > index) && (hash > deleteIndex || deleteIndex > index)) continue;
            this.keys[deleteIndex] = this.keys[index];
            this.values[deleteIndex] = this.values[index];
            this.values[index] = null;
            deleteIndex = index;
        }
    }

    @SuppressFBWarnings(value={"PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS"}, justification="deliberate, documented choice")
    private class EntryIterator
    extends AbstractIterator<Map.Entry<Long, V>>
    implements Map.Entry<Long, V> {
        private EntryIterator() {
        }

        @Override
        public Map.Entry<Long, V> next() {
            this.findNext();
            return this;
        }

        @Override
        public Long getKey() {
            return this.keys[this.getPosition()];
        }

        @Override
        public V getValue() {
            return this.values[this.getPosition()];
        }

        @Override
        public V setValue(V value) {
            Preconditions.checkNotNull(value);
            int pos = this.getPosition();
            Object oldValue = this.values[pos];
            this.values[pos] = value;
            return oldValue;
        }
    }

    public class KeyIterator
    extends AbstractIterator<Long> {
        @Override
        public Long next() {
            return this.nextLong();
        }

        public long nextLong() {
            this.findNext();
            return this.keys[this.getPosition()];
        }
    }

    private class ValueIterator<T>
    extends AbstractIterator<T> {
        private ValueIterator() {
        }

        @Override
        public T next() {
            this.findNext();
            return (T)this.values[this.getPosition()];
        }
    }

    private abstract class AbstractIterator<T>
    implements Iterator<T> {
        protected final long[] keys;
        protected final Object[] values;
        private int posCounter;
        private int stopCounter;
        private boolean isPositionValid;

        protected AbstractIterator() {
            this.keys = Long2ObjectHashMap.this.keys;
            this.values = Long2ObjectHashMap.this.values;
            int i = Long2ObjectHashMap.this.capacity;
            if (null != this.values[Long2ObjectHashMap.this.capacity - 1]) {
                int size = Long2ObjectHashMap.this.capacity;
                for (i = 0; i < size && null != this.values[i]; ++i) {
                }
            }
            this.stopCounter = i;
            this.posCounter = i + Long2ObjectHashMap.this.capacity;
        }

        protected int getPosition() {
            return this.posCounter & Long2ObjectHashMap.this.mask;
        }

        @Override
        public boolean hasNext() {
            for (int i = this.posCounter - 1; i >= this.stopCounter; --i) {
                int index = i & Long2ObjectHashMap.this.mask;
                if (null == this.values[index]) continue;
                return true;
            }
            return false;
        }

        protected void findNext() {
            this.isPositionValid = false;
            for (int i = this.posCounter - 1; i >= this.stopCounter; --i) {
                int index = i & Long2ObjectHashMap.this.mask;
                if (null == this.values[index]) continue;
                this.posCounter = i;
                this.isPositionValid = true;
                return;
            }
            throw new NoSuchElementException();
        }

        @Override
        public abstract T next();

        @Override
        public void remove() {
            if (!this.isPositionValid) {
                throw new IllegalStateException();
            }
            int position = this.getPosition();
            this.values[position] = null;
            --Long2ObjectHashMap.this.size;
            Long2ObjectHashMap.this.compactChain(position);
            this.isPositionValid = false;
        }
    }

    private class EntrySet
    extends AbstractSet<Map.Entry<Long, V>> {
        private EntrySet() {
        }

        @Override
        public int size() {
            return Long2ObjectHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return Long2ObjectHashMap.this.isEmpty();
        }

        @Override
        public Iterator<Map.Entry<Long, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public void clear() {
            Long2ObjectHashMap.this.clear();
        }
    }

    private class ValueCollection
    extends AbstractCollection<V> {
        private ValueCollection() {
        }

        @Override
        public int size() {
            return Long2ObjectHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return Long2ObjectHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return Long2ObjectHashMap.this.containsValue(o);
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public void clear() {
            Long2ObjectHashMap.this.clear();
        }
    }

    public class KeySet
    extends AbstractSet<Long> {
        @Override
        public int size() {
            return Long2ObjectHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return Long2ObjectHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return Long2ObjectHashMap.this.containsKey(o);
        }

        public boolean contains(long key) {
            return Long2ObjectHashMap.this.containsKey(key);
        }

        public KeyIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public boolean remove(Object o) {
            return null != Long2ObjectHashMap.this.remove(o);
        }

        public boolean remove(long key) {
            return null != Long2ObjectHashMap.this.remove(key);
        }

        @Override
        public void clear() {
            Long2ObjectHashMap.this.clear();
        }
    }
}

