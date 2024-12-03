/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.CompactHashMap;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
class CompactLinkedHashMap<K, V>
extends CompactHashMap<K, V> {
    private static final int ENDPOINT = -2;
    @CheckForNull
    @VisibleForTesting
    transient long[] links;
    private transient int firstEntry;
    private transient int lastEntry;
    private final boolean accessOrder;

    public static <K, V> CompactLinkedHashMap<K, V> create() {
        return new CompactLinkedHashMap<K, V>();
    }

    public static <K, V> CompactLinkedHashMap<K, V> createWithExpectedSize(int expectedSize) {
        return new CompactLinkedHashMap<K, V>(expectedSize);
    }

    CompactLinkedHashMap() {
        this(3);
    }

    CompactLinkedHashMap(int expectedSize) {
        this(expectedSize, false);
    }

    CompactLinkedHashMap(int expectedSize, boolean accessOrder) {
        super(expectedSize);
        this.accessOrder = accessOrder;
    }

    @Override
    void init(int expectedSize) {
        super.init(expectedSize);
        this.firstEntry = -2;
        this.lastEntry = -2;
    }

    @Override
    int allocArrays() {
        int expectedSize = super.allocArrays();
        this.links = new long[expectedSize];
        return expectedSize;
    }

    @Override
    Map<K, V> createHashFloodingResistantDelegate(int tableSize) {
        return new LinkedHashMap(tableSize, 1.0f, this.accessOrder);
    }

    @Override
    @CanIgnoreReturnValue
    Map<K, V> convertToHashFloodingResistantImplementation() {
        Map result = super.convertToHashFloodingResistantImplementation();
        this.links = null;
        return result;
    }

    private int getPredecessor(int entry) {
        return (int)(this.link(entry) >>> 32) - 1;
    }

    @Override
    int getSuccessor(int entry) {
        return (int)this.link(entry) - 1;
    }

    private void setSuccessor(int entry, int succ) {
        long succMask = 0xFFFFFFFFL;
        this.setLink(entry, this.link(entry) & (succMask ^ 0xFFFFFFFFFFFFFFFFL) | (long)(succ + 1) & succMask);
    }

    private void setPredecessor(int entry, int pred) {
        long predMask = -4294967296L;
        this.setLink(entry, this.link(entry) & (predMask ^ 0xFFFFFFFFFFFFFFFFL) | (long)(pred + 1) << 32);
    }

    private void setSucceeds(int pred, int succ) {
        if (pred == -2) {
            this.firstEntry = succ;
        } else {
            this.setSuccessor(pred, succ);
        }
        if (succ == -2) {
            this.lastEntry = pred;
        } else {
            this.setPredecessor(succ, pred);
        }
    }

    @Override
    void insertEntry(int entryIndex, @ParametricNullness K key, @ParametricNullness V value, int hash, int mask) {
        super.insertEntry(entryIndex, key, value, hash, mask);
        this.setSucceeds(this.lastEntry, entryIndex);
        this.setSucceeds(entryIndex, -2);
    }

    @Override
    void accessEntry(int index) {
        if (this.accessOrder) {
            this.setSucceeds(this.getPredecessor(index), this.getSuccessor(index));
            this.setSucceeds(this.lastEntry, index);
            this.setSucceeds(index, -2);
            this.incrementModCount();
        }
    }

    @Override
    void moveLastEntry(int dstIndex, int mask) {
        int srcIndex = this.size() - 1;
        super.moveLastEntry(dstIndex, mask);
        this.setSucceeds(this.getPredecessor(dstIndex), this.getSuccessor(dstIndex));
        if (dstIndex < srcIndex) {
            this.setSucceeds(this.getPredecessor(srcIndex), dstIndex);
            this.setSucceeds(dstIndex, this.getSuccessor(srcIndex));
        }
        this.setLink(srcIndex, 0L);
    }

    @Override
    void resizeEntries(int newCapacity) {
        super.resizeEntries(newCapacity);
        this.links = Arrays.copyOf(this.requireLinks(), newCapacity);
    }

    @Override
    int firstEntryIndex() {
        return this.firstEntry;
    }

    @Override
    int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
        return indexBeforeRemove >= this.size() ? indexRemoved : indexBeforeRemove;
    }

    @Override
    Set<Map.Entry<K, V>> createEntrySet() {
        class EntrySetImpl
        extends CompactHashMap.EntrySetView {
            EntrySetImpl(CompactLinkedHashMap this$0) {
            }

            @Override
            public Spliterator<Map.Entry<K, V>> spliterator() {
                return Spliterators.spliterator(this, 17);
            }
        }
        return new EntrySetImpl(this);
    }

    @Override
    Set<K> createKeySet() {
        class KeySetImpl
        extends CompactHashMap.KeySetView {
            KeySetImpl(CompactLinkedHashMap this$0) {
            }

            @Override
            public @Nullable Object[] toArray() {
                return ObjectArrays.toArrayImpl(this);
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return ObjectArrays.toArrayImpl(this, a);
            }

            @Override
            public Spliterator<K> spliterator() {
                return Spliterators.spliterator(this, 17);
            }
        }
        return new KeySetImpl(this);
    }

    @Override
    Collection<V> createValues() {
        class ValuesImpl
        extends CompactHashMap.ValuesView {
            ValuesImpl(CompactLinkedHashMap this$0) {
            }

            @Override
            public @Nullable Object[] toArray() {
                return ObjectArrays.toArrayImpl(this);
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return ObjectArrays.toArrayImpl(this, a);
            }

            @Override
            public Spliterator<V> spliterator() {
                return Spliterators.spliterator(this, 16);
            }
        }
        return new ValuesImpl(this);
    }

    @Override
    public void clear() {
        if (this.needsAllocArrays()) {
            return;
        }
        this.firstEntry = -2;
        this.lastEntry = -2;
        if (this.links != null) {
            Arrays.fill(this.links, 0, this.size(), 0L);
        }
        super.clear();
    }

    private long[] requireLinks() {
        return Objects.requireNonNull(this.links);
    }

    private long link(int i) {
        return this.requireLinks()[i];
    }

    private void setLink(int i, long value) {
        this.requireLinks()[i] = value;
    }
}

