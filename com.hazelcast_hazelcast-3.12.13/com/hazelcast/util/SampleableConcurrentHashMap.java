/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.internal.eviction.Expirable;
import com.hazelcast.internal.util.ThreadLocalRandomProvider;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@SerializableByConvention
public class SampleableConcurrentHashMap<K, V>
extends ConcurrentReferenceHashMap<K, V> {
    private static final float LOAD_FACTOR = 0.91f;

    public SampleableConcurrentHashMap(int initialCapacity) {
        this(initialCapacity, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.STRONG);
    }

    public SampleableConcurrentHashMap(int initialCapacity, ConcurrentReferenceHashMap.ReferenceType keyType, ConcurrentReferenceHashMap.ReferenceType valueType) {
        this(initialCapacity, 0.91f, 1, keyType, valueType, null);
    }

    private SampleableConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ConcurrentReferenceHashMap.ReferenceType keyType, ConcurrentReferenceHashMap.ReferenceType valueType, EnumSet<ConcurrentReferenceHashMap.Option> options) {
        super(initialCapacity, loadFactor, concurrencyLevel, keyType, valueType, options);
    }

    public int fetchKeys(int tableIndex, int size, List<K> keys) {
        long now = Clock.currentTimeMillis();
        ConcurrentReferenceHashMap.Segment segment = this.segments[0];
        ConcurrentReferenceHashMap.HashEntry<K, V>[] currentTable = segment.table;
        int nextTableIndex = tableIndex >= 0 && tableIndex < segment.table.length ? tableIndex : currentTable.length - 1;
        int counter = 0;
        while (nextTableIndex >= 0 && counter < size) {
            ConcurrentReferenceHashMap.HashEntry nextEntry = currentTable[nextTableIndex--];
            while (nextEntry != null) {
                Object value;
                if (nextEntry.key() != null && this.isValidForFetching(value = nextEntry.value(), now)) {
                    keys.add(nextEntry.key());
                    ++counter;
                }
                nextEntry = nextEntry.next;
            }
        }
        return nextTableIndex;
    }

    public int fetchEntries(int tableIndex, int size, List<Map.Entry<K, V>> entries) {
        long now = Clock.currentTimeMillis();
        ConcurrentReferenceHashMap.Segment segment = this.segments[0];
        ConcurrentReferenceHashMap.HashEntry<K, V>[] currentTable = segment.table;
        int nextTableIndex = tableIndex >= 0 && tableIndex < segment.table.length ? tableIndex : currentTable.length - 1;
        int counter = 0;
        while (nextTableIndex >= 0 && counter < size) {
            ConcurrentReferenceHashMap.HashEntry nextEntry = currentTable[nextTableIndex--];
            while (nextEntry != null) {
                Object value;
                if (nextEntry.key() != null && this.isValidForFetching(value = nextEntry.value(), now)) {
                    Object key = nextEntry.key();
                    entries.add(new AbstractMap.SimpleEntry(key, value));
                    ++counter;
                }
                nextEntry = nextEntry.next;
            }
        }
        return nextTableIndex;
    }

    protected boolean isValidForFetching(V value, long now) {
        if (value instanceof Expirable) {
            return !((Expirable)value).isExpiredAt(now);
        }
        return true;
    }

    protected <E extends SamplingEntry> E createSamplingEntry(K key, V value) {
        return (E)new SamplingEntry<K, V>(key, value);
    }

    public <E extends SamplingEntry> Iterable<E> getRandomSamples(int sampleCount) {
        if (sampleCount < 0) {
            throw new IllegalArgumentException("Sample count cannot be a negative value.");
        }
        if (sampleCount == 0 || this.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return new LazySamplingEntryIterableIterator(sampleCount);
    }

    protected boolean isValidForSampling(K key, V value) {
        return key != null && value != null;
    }

    private final class LazySamplingEntryIterableIterator<E extends SamplingEntry>
    implements Iterable<E>,
    Iterator<E> {
        private final int maxEntryCount;
        private final int randomNumber;
        private final int firstSegmentIndex;
        private int currentSegmentIndex;
        private int currentBucketIndex;
        private ConcurrentReferenceHashMap.HashEntry<K, V> mostRecentlyReturnedEntry;
        private int returnedEntryCount;
        private boolean reachedToEnd;
        private E currentSample;

        private LazySamplingEntryIterableIterator(int maxEntryCount) {
            this.maxEntryCount = maxEntryCount;
            this.randomNumber = ThreadLocalRandomProvider.get().nextInt(Integer.MAX_VALUE);
            this.currentSegmentIndex = this.firstSegmentIndex = this.randomNumber % SampleableConcurrentHashMap.this.segments.length;
            this.currentBucketIndex = -1;
        }

        @Override
        public Iterator<E> iterator() {
            return this;
        }

        private void iterate() {
            if (this.returnedEntryCount >= this.maxEntryCount || this.reachedToEnd) {
                this.currentSample = null;
                return;
            }
            do {
                ConcurrentReferenceHashMap.Segment segment;
                if ((segment = SampleableConcurrentHashMap.this.segments[this.currentSegmentIndex]) != null) {
                    ConcurrentReferenceHashMap.HashEntry<K, V>[] table = segment.table;
                    int firstBucketIndex = this.randomNumber % table.length;
                    if (this.currentBucketIndex == -1) {
                        this.currentBucketIndex = firstBucketIndex;
                    }
                    do {
                        this.mostRecentlyReturnedEntry = this.mostRecentlyReturnedEntry == null ? table[this.currentBucketIndex] : this.mostRecentlyReturnedEntry.next;
                        while (this.mostRecentlyReturnedEntry != null) {
                            Object value = this.mostRecentlyReturnedEntry.value();
                            Object key = this.mostRecentlyReturnedEntry.key();
                            if (SampleableConcurrentHashMap.this.isValidForSampling(key, value)) {
                                this.currentSample = SampleableConcurrentHashMap.this.createSamplingEntry(key, value);
                                ++this.returnedEntryCount;
                                return;
                            }
                            this.mostRecentlyReturnedEntry = this.mostRecentlyReturnedEntry.next;
                        }
                        int n = this.currentBucketIndex = ++this.currentBucketIndex < table.length ? this.currentBucketIndex : 0;
                    } while (this.currentBucketIndex != firstBucketIndex);
                }
                this.currentSegmentIndex = ++this.currentSegmentIndex < SampleableConcurrentHashMap.this.segments.length ? this.currentSegmentIndex : 0;
                this.currentBucketIndex = -1;
                this.mostRecentlyReturnedEntry = null;
            } while (this.currentSegmentIndex != this.firstSegmentIndex);
            this.reachedToEnd = true;
            this.currentSample = null;
        }

        @Override
        public boolean hasNext() {
            if (this.currentSample == null) {
                this.iterate();
            }
            return this.currentSample != null;
        }

        @Override
        public E next() {
            if (this.hasNext()) {
                E returnValue = this.currentSample;
                this.currentSample = null;
                return returnValue;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing is not supported");
        }
    }

    public static class SamplingEntry<K, V> {
        protected final K key;
        protected final V value;

        public SamplingEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getEntryKey() {
            return this.key;
        }

        public V getEntryValue() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (!(o instanceof SamplingEntry)) {
                return false;
            }
            SamplingEntry e = (SamplingEntry)o;
            return SamplingEntry.eq(this.key, e.key) && SamplingEntry.eq(this.value, e.value);
        }

        private static boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }

        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}

