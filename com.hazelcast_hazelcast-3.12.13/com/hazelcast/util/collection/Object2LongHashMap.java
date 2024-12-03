/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.QuickMath;
import com.hazelcast.util.collection.Hashing;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Object2LongHashMap<K>
implements Map<K, Long> {
    private static final float DEFAULT_LOAD_FACTOR = 0.6f;
    private static final int MIN_CAPACITY = 8;
    private final float loadFactor;
    private final long missingValue;
    private int resizeThreshold;
    private int size;
    private final boolean shouldAvoidAllocation;
    private K[] keys;
    private long[] values;
    private ValueCollection valueCollection;
    private KeySet keySet;
    private EntrySet entrySet;

    public Object2LongHashMap(long missingValue) {
        this(8, 0.6f, missingValue);
    }

    public Object2LongHashMap(int initialCapacity, float loadFactor, long missingValue) {
        this(initialCapacity, loadFactor, missingValue, true);
    }

    public Object2LongHashMap(int initialCapacity, float loadFactor, long missingValue, boolean shouldAvoidAllocation) {
        this.loadFactor = loadFactor;
        int capacity = QuickMath.nextPowerOfTwo(Math.max(8, initialCapacity));
        this.resizeThreshold = (int)((float)capacity * loadFactor);
        this.missingValue = missingValue;
        this.shouldAvoidAllocation = shouldAvoidAllocation;
        this.keys = new Object[capacity];
        this.values = new long[capacity];
        Arrays.fill(this.values, missingValue);
    }

    public Object2LongHashMap(Object2LongHashMap<K> mapToCopy) {
        this.loadFactor = mapToCopy.loadFactor;
        this.resizeThreshold = mapToCopy.resizeThreshold;
        this.size = mapToCopy.size;
        this.missingValue = mapToCopy.missingValue;
        this.shouldAvoidAllocation = mapToCopy.shouldAvoidAllocation;
        this.keys = (Object[])mapToCopy.keys.clone();
        this.values = (long[])mapToCopy.values.clone();
    }

    public long missingValue() {
        return this.missingValue;
    }

    public float loadFactor() {
        return this.loadFactor;
    }

    public int capacity() {
        return this.values.length;
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
        int mask = this.values.length - 1;
        int index = Hashing.hash(key, mask);
        boolean found = false;
        while (this.missingValue != this.values[index]) {
            if (key.equals(this.keys[index])) {
                found = true;
                break;
            }
            ++index;
            index &= mask;
        }
        return found;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.containsValue((Long)value);
    }

    public boolean containsValue(long value) {
        if (value == this.missingValue) {
            return false;
        }
        boolean found = false;
        for (long v : this.values) {
            if (value != v) continue;
            found = true;
            break;
        }
        return found;
    }

    @Override
    public Long get(Object key) {
        return this.valOrNull(this.getValue(key));
    }

    public long getValue(K key) {
        long value;
        int mask = this.values.length - 1;
        int index = Hashing.hash(key, mask);
        while (this.missingValue != (value = this.values[index]) && !key.equals(this.keys[index])) {
            ++index;
            index &= mask;
        }
        return value;
    }

    @Override
    public Long put(K key, Long value) {
        return this.valOrNull(this.put(key, (long)value));
    }

    @Override
    public long put(K key, long value) {
        if (value == this.missingValue) {
            throw new IllegalArgumentException("cannot accept missingValue");
        }
        long oldValue = this.missingValue;
        int mask = this.values.length - 1;
        int index = Hashing.hash(key, mask);
        while (this.missingValue != this.values[index]) {
            if (key.equals(this.keys[index])) {
                oldValue = this.values[index];
                break;
            }
            ++index;
            index &= mask;
        }
        if (this.missingValue == oldValue) {
            ++this.size;
            this.keys[index] = key;
        }
        this.values[index] = value;
        if (this.size > this.resizeThreshold) {
            this.increaseCapacity();
        }
        return oldValue;
    }

    @Override
    public Long remove(Object key) {
        return this.valOrNull(this.removeKey(key));
    }

    public long removeKey(K key) {
        long value;
        int mask = this.values.length - 1;
        int index = Hashing.hash(key, mask);
        while (this.missingValue != (value = this.values[index])) {
            if (key.equals(this.keys[index])) {
                this.keys[index] = null;
                this.values[index] = this.missingValue;
                --this.size;
                this.compactChain(index);
                break;
            }
            ++index;
            index &= mask;
        }
        return value;
    }

    @Override
    public void clear() {
        if (this.size > 0) {
            Arrays.fill(this.keys, null);
            Arrays.fill(this.values, this.missingValue);
            this.size = 0;
        }
    }

    public void compact() {
        int idealCapacity = (int)Math.round((double)this.size() * (1.0 / (double)this.loadFactor));
        this.rehash(QuickMath.nextPowerOfTwo(Math.max(8, idealCapacity)));
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> map) {
        for (Map.Entry<K, Long> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public KeySet keySet() {
        if (null == this.keySet) {
            this.keySet = new KeySet();
        }
        return this.keySet;
    }

    public ValueCollection values() {
        if (null == this.valueCollection) {
            this.valueCollection = new ValueCollection();
        }
        return this.valueCollection;
    }

    public EntrySet entrySet() {
        if (null == this.entrySet) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }

    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        EntryIterator entryIterator = new EntryIterator();
        entryIterator.reset();
        StringBuilder sb = new StringBuilder().append('{');
        while (true) {
            entryIterator.next();
            sb.append(entryIterator.getKey()).append('=').append(entryIterator.getLongValue());
            if (!entryIterator.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map that = (Map)o;
        if (this.size != that.size()) {
            return false;
        }
        int length = this.values.length;
        for (int i = 0; i < length; ++i) {
            long thisValue = this.values[i];
            if (this.missingValue == thisValue) continue;
            Object thatValueObject = that.get(this.keys[i]);
            if (!(thatValueObject instanceof Long)) {
                return false;
            }
            long thatValue = (Long)thatValueObject;
            if (this.missingValue != thatValue && thisValue == thatValue) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        int length = this.values.length;
        for (int i = 0; i < length; ++i) {
            long value = this.values[i];
            if (this.missingValue == value) continue;
            result += this.keys[i].hashCode() ^ Hashing.hashCode(value);
        }
        return result;
    }

    @Override
    public long replace(K key, long value) {
        long curValue = this.getValue(key);
        if (curValue != this.missingValue) {
            curValue = this.put(key, value);
        }
        return curValue;
    }

    @Override
    public boolean replace(K key, long oldValue, long newValue) {
        long curValue = this.getValue(key);
        if (curValue == this.missingValue || curValue != oldValue) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    private void increaseCapacity() {
        int newCapacity = this.values.length << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("max capacity reached at size=" + this.size);
        }
        this.rehash(newCapacity);
    }

    private void rehash(int newCapacity) {
        int mask = newCapacity - 1;
        this.resizeThreshold = (int)((float)newCapacity * this.loadFactor);
        Object[] tempKeys = new Object[newCapacity];
        long[] tempValues = new long[newCapacity];
        Arrays.fill(tempValues, this.missingValue);
        int size = this.values.length;
        for (int i = 0; i < size; ++i) {
            long value = this.values[i];
            if (this.missingValue == value) continue;
            K key = this.keys[i];
            int index = Hashing.hash(key, mask);
            while (this.missingValue != tempValues[index]) {
                ++index;
                index &= mask;
            }
            tempKeys[index] = key;
            tempValues[index] = value;
        }
        this.keys = tempKeys;
        this.values = tempValues;
    }

    private void compactChain(int deleteIndex) {
        int mask = this.values.length - 1;
        int index = deleteIndex;
        while (true) {
            ++index;
            if (this.missingValue == this.values[index &= mask]) break;
            int hash = Hashing.hash(this.keys[index], mask);
            if ((index >= hash || hash > deleteIndex && deleteIndex > index) && (hash > deleteIndex || deleteIndex > index)) continue;
            this.keys[deleteIndex] = this.keys[index];
            this.values[deleteIndex] = this.values[index];
            this.keys[index] = null;
            this.values[index] = this.missingValue;
            deleteIndex = index;
        }
    }

    private Long valOrNull(long value) {
        return value == this.missingValue ? null : Long.valueOf(value);
    }

    @SuppressFBWarnings(value={"PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS"})
    public final class EntryIterator
    extends AbstractIterator<Map.Entry<K, Long>>
    implements Map.Entry<K, Long> {
        @Override
        public Map.Entry<K, Long> next() {
            this.findNext();
            if (Object2LongHashMap.this.shouldAvoidAllocation) {
                return this;
            }
            return this.allocateDuplicateEntry();
        }

        private Map.Entry<K, Long> allocateDuplicateEntry() {
            final Object k = this.getKey();
            final long v = this.getLongValue();
            return new Map.Entry<K, Long>(){

                @Override
                public K getKey() {
                    return k;
                }

                @Override
                public Long getValue() {
                    return v;
                }

                @Override
                public Long setValue(Long value) {
                    return Object2LongHashMap.this.put(k, value);
                }

                @Override
                public int hashCode() {
                    return this.getKey().hashCode() ^ Hashing.hashCode(EntryIterator.this.getLongValue());
                }

                @Override
                public boolean equals(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    return e.getKey() != null && e.getValue() != null && e.getKey().equals(k) && e.getValue().equals(v);
                }

                public String toString() {
                    return k + "=" + v;
                }
            };
        }

        @Override
        public K getKey() {
            return Object2LongHashMap.this.keys[this.position()];
        }

        public long getLongValue() {
            return Object2LongHashMap.this.values[this.position()];
        }

        @Override
        public Long getValue() {
            return this.getLongValue();
        }

        @Override
        public Long setValue(Long value) {
            return this.setValue((long)value);
        }

        @Override
        public long setValue(long value) {
            if (value == Object2LongHashMap.this.missingValue) {
                throw new IllegalArgumentException("cannot accept missingValue");
            }
            int pos = this.position();
            long oldValue = Object2LongHashMap.this.values[pos];
            ((Object2LongHashMap)Object2LongHashMap.this).values[pos] = value;
            return oldValue;
        }
    }

    public final class KeyIterator
    extends AbstractIterator<K> {
        @Override
        public K next() {
            this.findNext();
            return Object2LongHashMap.this.keys[this.position()];
        }
    }

    public final class ValueIterator
    extends AbstractIterator<Long> {
        @Override
        public Long next() {
            return this.nextLong();
        }

        public long nextLong() {
            this.findNext();
            return Object2LongHashMap.this.values[this.position()];
        }
    }

    abstract class AbstractIterator<T>
    implements Iterator<T> {
        private int posCounter;
        private int stopCounter;
        private int remaining;
        private boolean isPositionValid = false;

        AbstractIterator() {
        }

        protected final int position() {
            return this.posCounter & Object2LongHashMap.this.values.length - 1;
        }

        @Override
        public boolean hasNext() {
            return this.remaining > 0;
        }

        protected final void findNext() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            long[] values = Object2LongHashMap.this.values;
            int mask = values.length - 1;
            for (int i = this.posCounter - 1; i >= this.stopCounter; --i) {
                int index = i & mask;
                if (Object2LongHashMap.this.missingValue == values[index]) continue;
                this.posCounter = i;
                this.isPositionValid = true;
                --this.remaining;
                return;
            }
            this.isPositionValid = false;
            throw new IllegalStateException();
        }

        @Override
        public abstract T next();

        @Override
        public void remove() {
            if (!this.isPositionValid) {
                throw new IllegalStateException();
            }
            int position = this.position();
            ((Object2LongHashMap)Object2LongHashMap.this).values[position] = Object2LongHashMap.this.missingValue;
            ((Object2LongHashMap)Object2LongHashMap.this).keys[position] = null;
            --Object2LongHashMap.this.size;
            Object2LongHashMap.this.compactChain(position);
            this.isPositionValid = false;
        }

        final void reset() {
            int capacity;
            this.remaining = Object2LongHashMap.this.size;
            long[] values = Object2LongHashMap.this.values;
            int i = capacity = values.length;
            if (Object2LongHashMap.this.missingValue != values[capacity - 1]) {
                for (i = 0; i < capacity && Object2LongHashMap.this.missingValue != values[i]; ++i) {
                }
            }
            this.stopCounter = i;
            this.posCounter = i + capacity;
            this.isPositionValid = false;
        }
    }

    public final class EntrySet
    extends AbstractSet<Map.Entry<K, Long>> {
        private final EntryIterator entryIterator;

        public EntrySet() {
            this.entryIterator = Object2LongHashMap.this.shouldAvoidAllocation ? new EntryIterator() : null;
        }

        public EntryIterator iterator() {
            EntryIterator entryIterator = this.entryIterator;
            if (null == entryIterator) {
                entryIterator = new EntryIterator();
            }
            entryIterator.reset();
            return entryIterator;
        }

        @Override
        public int size() {
            return Object2LongHashMap.this.size();
        }

        @Override
        public void clear() {
            Object2LongHashMap.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            Map.Entry entry = (Map.Entry)o;
            Long value = Object2LongHashMap.this.get(entry.getKey());
            return value != null && value.equals(entry.getValue());
        }
    }

    public final class ValueCollection
    extends AbstractCollection<Long> {
        private final ValueIterator valueIterator;

        public ValueCollection() {
            this.valueIterator = Object2LongHashMap.this.shouldAvoidAllocation ? new ValueIterator() : null;
        }

        public ValueIterator iterator() {
            ValueIterator valueIterator = this.valueIterator;
            if (null == valueIterator) {
                valueIterator = new ValueIterator();
            }
            valueIterator.reset();
            return valueIterator;
        }

        @Override
        public int size() {
            return Object2LongHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return Object2LongHashMap.this.containsValue(o);
        }

        @Override
        public void clear() {
            Object2LongHashMap.this.clear();
        }
    }

    public final class KeySet
    extends AbstractSet<K> {
        private final KeyIterator keyIterator;

        public KeySet() {
            this.keyIterator = Object2LongHashMap.this.shouldAvoidAllocation ? new KeyIterator() : null;
        }

        public KeyIterator iterator() {
            KeyIterator keyIterator = this.keyIterator;
            if (null == keyIterator) {
                keyIterator = new KeyIterator();
            }
            keyIterator.reset();
            return keyIterator;
        }

        @Override
        public int size() {
            return Object2LongHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return Object2LongHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return Object2LongHashMap.this.missingValue != Object2LongHashMap.this.removeKey(o);
        }

        @Override
        public void clear() {
            Object2LongHashMap.this.clear();
        }
    }
}

