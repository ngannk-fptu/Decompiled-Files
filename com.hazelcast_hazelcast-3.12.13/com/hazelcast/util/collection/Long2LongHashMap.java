/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.QuickMath;
import com.hazelcast.util.collection.Hashing;
import com.hazelcast.util.collection.MapDelegatingSet;
import com.hazelcast.util.function.BiConsumer;
import com.hazelcast.util.function.LongLongConsumer;
import com.hazelcast.util.function.Predicate;
import com.hazelcast.util.function.Supplier;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Long2LongHashMap
implements Map<Long, Long> {
    public static final double DEFAULT_LOAD_FACTOR = 0.6;
    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final int CURSOR_BEFORE_FIRST_INDEX = -2;
    private final Set<Long> keySet;
    private final LongIterator valueIterator;
    private final Collection<Long> values;
    private final Set<Map.Entry<Long, Long>> entrySet = this.entrySetSingleton();
    private final double loadFactor;
    private final long missingValue;
    private long[] entries;
    private int capacity;
    private int mask;
    private int resizeThreshold;
    private int size;

    public Long2LongHashMap(int initialCapacity, double loadFactor, long missingValue) {
        this(loadFactor, missingValue);
        this.capacity(QuickMath.nextPowerOfTwo(initialCapacity));
    }

    public Long2LongHashMap(long missingValue) {
        this(8, 0.6, missingValue);
    }

    public Long2LongHashMap(Long2LongHashMap that) {
        this(that.loadFactor, that.missingValue);
        this.entries = Arrays.copyOf(that.entries, that.entries.length);
        this.capacity = that.capacity;
        this.mask = that.mask;
        this.resizeThreshold = that.resizeThreshold;
        this.size = that.size;
    }

    private Long2LongHashMap(double loadFactor, long missingValue) {
        this.keySet = this.keySetSingleton();
        this.values = this.valuesSingleton();
        this.valueIterator = new LongIterator(1);
        this.loadFactor = loadFactor;
        this.missingValue = missingValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    public long get(long key) {
        long candidateKey;
        long[] entries = this.entries;
        int index = Hashing.evenLongHash(key, this.mask);
        while ((candidateKey = entries[index]) != this.missingValue) {
            if (candidateKey == key) {
                return entries[index + 1];
            }
            index = this.next(index);
        }
        return this.missingValue;
    }

    @Override
    public long put(long key, long value) {
        long candidateKey;
        assert (key != this.missingValue) : "Invalid key " + key;
        assert (value != this.missingValue) : "Invalid value " + value;
        long oldValue = this.missingValue;
        int index = Hashing.evenLongHash(key, this.mask);
        while ((candidateKey = this.entries[index]) != this.missingValue) {
            if (candidateKey == key) {
                oldValue = this.entries[index + 1];
                break;
            }
            index = this.next(index);
        }
        if (oldValue == this.missingValue) {
            ++this.size;
            this.entries[index] = key;
        }
        this.entries[index + 1] = value;
        this.checkResize();
        return oldValue;
    }

    private void checkResize() {
        if (this.size > this.resizeThreshold) {
            int newCapacity = this.capacity << 1;
            if (newCapacity < 0) {
                throw new IllegalStateException("Max capacity reached at size=" + this.size);
            }
            this.rehash(newCapacity);
        }
    }

    private void rehash(int newCapacity) {
        long[] oldEntries = this.entries;
        this.capacity(newCapacity);
        for (int i = 0; i < oldEntries.length; i += 2) {
            long key = oldEntries[i];
            if (key == this.missingValue) continue;
            this.put(key, oldEntries[i + 1]);
        }
    }

    public void longForEach(LongLongConsumer consumer) {
        long[] entries = this.entries;
        for (int i = 0; i < entries.length; i += 2) {
            long key = entries[i];
            if (key == this.missingValue) continue;
            consumer.accept(entries[i], entries[i + 1]);
        }
    }

    public LongLongCursor cursor() {
        return new LongLongCursor();
    }

    public boolean containsKey(long key) {
        return this.get(key) != this.missingValue;
    }

    public boolean containsValue(long value) {
        long[] entries = this.entries;
        for (int i = 1; i < entries.length; i += 2) {
            long entryValue = entries[i];
            if (entryValue != value) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        Arrays.fill(this.entries, this.missingValue);
        this.size = 0;
    }

    @Override
    public Long get(Object key) {
        return this.get((Long)key);
    }

    @Override
    public Long put(Long key, Long value) {
        return this.put((long)key, (long)value);
    }

    @Override
    public void forEach(BiConsumer<? super Long, ? super Long> action) {
        this.longForEach(new UnboxingBiConsumer(action));
    }

    @Override
    public boolean containsKey(Object key) {
        return this.containsKey((Long)key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.containsValue((Long)value);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Long> map) {
        for (Map.Entry<? extends Long, ? extends Long> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Long> keySet() {
        return this.keySet;
    }

    @Override
    public Collection<Long> values() {
        return this.values;
    }

    @Override
    public Set<Map.Entry<Long, Long>> entrySet() {
        return this.entrySet;
    }

    @Override
    public Long remove(Object key) {
        return this.remove((Long)key);
    }

    public long remove(long key) {
        long candidateKey;
        long[] entries = this.entries;
        int index = Hashing.evenLongHash(key, this.mask);
        while ((candidateKey = entries[index]) != this.missingValue) {
            if (candidateKey == key) {
                int valueIndex = index + 1;
                long oldValue = entries[valueIndex];
                entries[index] = this.missingValue;
                entries[valueIndex] = this.missingValue;
                --this.size;
                this.compactChain(index);
                return oldValue;
            }
            index = this.next(index);
        }
        return this.missingValue;
    }

    public String toString() {
        final StringBuilder b = new StringBuilder(this.size() * 8);
        b.append('{');
        this.longForEach(new LongLongConsumer(){
            String separator = "";

            @Override
            public void accept(long key, long value) {
                b.append(this.separator).append(key).append("->").append(value);
                this.separator = " ";
            }
        });
        return b.append('}').toString();
    }

    private void compactChain(int deleteIndex) {
        long[] entries = this.entries;
        int index = deleteIndex;
        while (entries[index = this.next(index)] != this.missingValue) {
            int hash = Hashing.evenLongHash(entries[index], this.mask);
            if ((index >= hash || hash > deleteIndex && deleteIndex > index) && (hash > deleteIndex || deleteIndex > index)) continue;
            entries[deleteIndex] = entries[index];
            entries[deleteIndex + 1] = entries[index + 1];
            entries[index] = this.missingValue;
            entries[index + 1] = this.missingValue;
            deleteIndex = index;
        }
        return;
    }

    private int next(int index) {
        return index + 2 & this.mask;
    }

    private void capacity(int newCapacity) {
        this.capacity = newCapacity;
        this.resizeThreshold = (int)((double)newCapacity * this.loadFactor);
        this.mask = newCapacity * 2 - 1;
        this.entries = new long[newCapacity * 2];
        this.size = 0;
        Arrays.fill(this.entries, this.missingValue);
    }

    private MapDelegatingSet<Map.Entry<Long, Long>> entrySetSingleton() {
        return new MapDelegatingSet<Map.Entry<Long, Long>>(this, new EntryIteratorSupplier(new EntryIterator()), new Predicate(){

            public boolean test(Object e) {
                return Long2LongHashMap.this.containsKey(((Map.Entry)e).getKey());
            }
        });
    }

    private MapDelegatingSet<Long> keySetSingleton() {
        return new MapDelegatingSet<Long>(this, new IteratorSupplier(new LongIterator(0)), new Predicate(){

            public boolean test(Object value) {
                return Long2LongHashMap.this.containsValue(value);
            }
        });
    }

    private MapDelegatingSet<Long> valuesSingleton() {
        return new MapDelegatingSet<Long>(this, new Supplier<Iterator<Long>>(){

            @Override
            public Iterator<Long> get() {
                return Long2LongHashMap.this.valueIterator.reset();
            }
        }, new Predicate(){

            public boolean test(Object key) {
                return Long2LongHashMap.this.containsKey(key);
            }
        });
    }

    @SuppressFBWarnings(value={"PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS"}, justification="deliberate, documented choice")
    private final class EntryIterator
    extends AbstractIterator
    implements Iterator<Map.Entry<Long, Long>>,
    Map.Entry<Long, Long> {
        private long key;
        private long value;

        private EntryIterator() {
        }

        @Override
        public Long getKey() {
            return this.key;
        }

        @Override
        public Long getValue() {
            return this.value;
        }

        @Override
        public Long setValue(Long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map.Entry<Long, Long> next() {
            this.findNext();
            int keyPosition = this.keyPosition();
            this.key = Long2LongHashMap.this.entries[keyPosition];
            this.value = Long2LongHashMap.this.entries[keyPosition + 1];
            return this;
        }

        public EntryIterator reset() {
            ((AbstractIterator)this).reset();
            this.key = Long2LongHashMap.this.missingValue;
            this.value = Long2LongHashMap.this.missingValue;
            return this;
        }
    }

    public final class LongIterator
    extends AbstractIterator
    implements Iterator<Long> {
        private final int offset;

        private LongIterator(int offset) {
            this.offset = offset;
        }

        @Override
        public Long next() {
            return this.nextValue();
        }

        public long nextValue() {
            this.findNext();
            return Long2LongHashMap.this.entries[this.keyPosition() + this.offset];
        }

        public LongIterator reset() {
            ((AbstractIterator)this).reset();
            return this;
        }
    }

    private abstract class AbstractIterator {
        private int capacity;
        private int mask;
        private int positionCounter;
        private int stopCounter;

        private AbstractIterator() {
        }

        private void reset() {
            long[] entries = Long2LongHashMap.this.entries;
            this.capacity = entries.length;
            this.mask = this.capacity - 1;
            int i = this.capacity;
            if (entries[this.capacity - 2] != Long2LongHashMap.this.missingValue) {
                int size = this.capacity;
                for (i = 0; i < size && entries[i] != Long2LongHashMap.this.missingValue; i += 2) {
                }
            }
            this.stopCounter = i;
            this.positionCounter = i + this.capacity;
        }

        protected int keyPosition() {
            return this.positionCounter & this.mask;
        }

        public boolean hasNext() {
            long[] entries = Long2LongHashMap.this.entries;
            boolean hasNext = false;
            for (int i = this.positionCounter - 2; i >= this.stopCounter; i -= 2) {
                int index = i & this.mask;
                if (entries[index] == Long2LongHashMap.this.missingValue) continue;
                hasNext = true;
                break;
            }
            return hasNext;
        }

        protected void findNext() {
            long[] entries = Long2LongHashMap.this.entries;
            for (int i = this.positionCounter - 2; i >= this.stopCounter; i -= 2) {
                int index = i & this.mask;
                if (entries[index] == Long2LongHashMap.this.missingValue) continue;
                this.positionCounter = i;
                return;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    private static class UnboxingBiConsumer
    implements LongLongConsumer {
        private final BiConsumer<? super Long, ? super Long> action;

        public UnboxingBiConsumer(BiConsumer<? super Long, ? super Long> action) {
            this.action = action;
        }

        @Override
        public void accept(long t, long u) {
            this.action.accept((Long)t, (Long)u);
        }
    }

    private static class EntryIteratorSupplier
    implements Supplier<Iterator<Map.Entry<Long, Long>>> {
        private final EntryIterator entryIterator;

        public EntryIteratorSupplier(EntryIterator entryIterator) {
            this.entryIterator = entryIterator;
        }

        @Override
        public Iterator<Map.Entry<Long, Long>> get() {
            return this.entryIterator.reset();
        }
    }

    private static class IteratorSupplier
    implements Supplier<Iterator<Long>> {
        private final LongIterator keyIterator;

        public IteratorSupplier(LongIterator keyIterator) {
            this.keyIterator = keyIterator;
        }

        @Override
        public Iterator<Long> get() {
            return this.keyIterator.reset();
        }
    }

    public final class LongLongCursor {
        private int i = -2;

        public boolean advance() {
            long[] es = Long2LongHashMap.this.entries;
            do {
                this.i += 2;
            } while (this.i < es.length && es[this.i] == Long2LongHashMap.this.missingValue);
            return this.i < es.length;
        }

        public long key() {
            return Long2LongHashMap.this.entries[this.i];
        }

        public long value() {
            return Long2LongHashMap.this.entries[this.i + 1];
        }
    }
}

