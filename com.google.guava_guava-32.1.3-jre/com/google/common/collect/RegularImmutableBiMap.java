/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableMapKeySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.JdkBackedImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true, emulated=true)
class RegularImmutableBiMap<K, V>
extends ImmutableBiMap<K, V> {
    static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap(null, null, ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
    static final double MAX_LOAD_FACTOR = 1.2;
    @CheckForNull
    private final transient @Nullable ImmutableMapEntry<K, V>[] keyTable;
    @CheckForNull
    private final transient @Nullable ImmutableMapEntry<K, V>[] valueTable;
    @VisibleForTesting
    final transient Map.Entry<K, V>[] entries;
    private final transient int mask;
    private final transient int hashCode;
    @LazyInit
    @CheckForNull
    @RetainedWith
    private transient ImmutableBiMap<V, K> inverse;

    static <K, V> ImmutableBiMap<K, V> fromEntries(Map.Entry<K, V> ... entries) {
        return RegularImmutableBiMap.fromEntryArray(entries.length, entries);
    }

    static <K, V> ImmutableBiMap<K, V> fromEntryArray(int n,  @Nullable Map.Entry<K, V>[] entryArray) {
        Preconditions.checkPositionIndex(n, entryArray.length);
        int tableSize = Hashing.closedTableSize(n, 1.2);
        int mask = tableSize - 1;
        @Nullable ImmutableMapEntry<K, V>[] keyTable = ImmutableMapEntry.createEntryArray(tableSize);
        @Nullable ImmutableMapEntry<K, V>[] valueTable = ImmutableMapEntry.createEntryArray(tableSize);
        Map.Entry<K, V>[] entries = n == entryArray.length ? entryArray : ImmutableMapEntry.createEntryArray(n);
        int hashCode = 0;
        for (int i = 0; i < n; ++i) {
            Map.Entry<K, V> entry = Objects.requireNonNull(entryArray[i]);
            K key = entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int keyHash = key.hashCode();
            int valueHash = value.hashCode();
            int keyBucket = Hashing.smear(keyHash) & mask;
            int valueBucket = Hashing.smear(valueHash) & mask;
            ImmutableMapEntry nextInKeyBucket = keyTable[keyBucket];
            ImmutableMapEntry nextInValueBucket = valueTable[valueBucket];
            try {
                RegularImmutableMap.checkNoConflictInKeyBucket(key, value, nextInKeyBucket, true);
                RegularImmutableBiMap.checkNoConflictInValueBucket(value, entry, nextInValueBucket);
            }
            catch (RegularImmutableMap.BucketOverflowException e) {
                return JdkBackedImmutableBiMap.create(n, entryArray);
            }
            ImmutableMapEntry newEntry = nextInValueBucket == null && nextInKeyBucket == null ? RegularImmutableMap.makeImmutable(entry, key, value) : new ImmutableMapEntry.NonTerminalImmutableBiMapEntry<K, V>(key, value, nextInKeyBucket, nextInValueBucket);
            keyTable[keyBucket] = newEntry;
            valueTable[valueBucket] = newEntry;
            entries[i] = newEntry;
            hashCode += keyHash ^ valueHash;
        }
        return new RegularImmutableBiMap(keyTable, valueTable, entries, mask, hashCode);
    }

    private RegularImmutableBiMap(@CheckForNull @Nullable ImmutableMapEntry<K, V>[] keyTable, @CheckForNull @Nullable ImmutableMapEntry<K, V>[] valueTable, Map.Entry<K, V>[] entries, int mask, int hashCode) {
        this.keyTable = keyTable;
        this.valueTable = valueTable;
        this.entries = entries;
        this.mask = mask;
        this.hashCode = hashCode;
    }

    private static void checkNoConflictInValueBucket(Object value, Map.Entry<?, ?> entry, @CheckForNull ImmutableMapEntry<?, ?> valueBucketHead) throws RegularImmutableMap.BucketOverflowException {
        int bucketSize = 0;
        while (valueBucketHead != null) {
            RegularImmutableBiMap.checkNoConflict(!value.equals(valueBucketHead.getValue()), "value", entry, valueBucketHead);
            if (++bucketSize > 8) {
                throw new RegularImmutableMap.BucketOverflowException();
            }
            valueBucketHead = valueBucketHead.getNextInValueBucket();
        }
    }

    @Override
    @CheckForNull
    public V get(@CheckForNull Object key) {
        return RegularImmutableMap.get(key, this.keyTable, this.mask);
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return this.isEmpty() ? ImmutableSet.of() : new ImmutableMapEntrySet.RegularEntrySet<K, V>(this, this.entries);
    }

    @Override
    ImmutableSet<K> createKeySet() {
        return new ImmutableMapKeySet(this);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        for (Map.Entry<K, V> entry : this.entries) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int size() {
        return this.entries.length;
    }

    @Override
    public ImmutableBiMap<V, K> inverse() {
        if (this.isEmpty()) {
            return ImmutableBiMap.of();
        }
        Inverse result = this.inverse;
        return result == null ? (this.inverse = new Inverse()) : result;
    }

    @J2ktIncompatible
    private static class InverseSerializedForm<K, V>
    implements Serializable {
        private final ImmutableBiMap<K, V> forward;
        private static final long serialVersionUID = 1L;

        InverseSerializedForm(ImmutableBiMap<K, V> forward) {
            this.forward = forward;
        }

        Object readResolve() {
            return this.forward.inverse();
        }
    }

    private final class Inverse
    extends ImmutableBiMap<V, K> {
        private Inverse() {
        }

        @Override
        public int size() {
            return this.inverse().size();
        }

        @Override
        public ImmutableBiMap<K, V> inverse() {
            return RegularImmutableBiMap.this;
        }

        @Override
        public void forEach(BiConsumer<? super V, ? super K> action) {
            Preconditions.checkNotNull(action);
            RegularImmutableBiMap.this.forEach((? super K k, ? super V v) -> action.accept((Object)v, (Object)k));
        }

        @Override
        @CheckForNull
        public K get(@CheckForNull Object value) {
            if (value == null || RegularImmutableBiMap.this.valueTable == null) {
                return null;
            }
            int bucket = Hashing.smear(value.hashCode()) & RegularImmutableBiMap.this.mask;
            for (ImmutableMapEntry entry = RegularImmutableBiMap.this.valueTable[bucket]; entry != null; entry = entry.getNextInValueBucket()) {
                if (!value.equals(entry.getValue())) continue;
                return entry.getKey();
            }
            return null;
        }

        @Override
        ImmutableSet<V> createKeySet() {
            return new ImmutableMapKeySet(this);
        }

        @Override
        ImmutableSet<Map.Entry<V, K>> createEntrySet() {
            return new InverseEntrySet();
        }

        @Override
        boolean isPartialView() {
            return false;
        }

        @Override
        @J2ktIncompatible
        Object writeReplace() {
            return new InverseSerializedForm(RegularImmutableBiMap.this);
        }

        @J2ktIncompatible
        private void readObject(ObjectInputStream stream) throws InvalidObjectException {
            throw new InvalidObjectException("Use InverseSerializedForm");
        }

        final class InverseEntrySet
        extends ImmutableMapEntrySet<V, K> {
            InverseEntrySet() {
            }

            @Override
            ImmutableMap<V, K> map() {
                return Inverse.this;
            }

            @Override
            boolean isHashCodeFast() {
                return true;
            }

            @Override
            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }

            @Override
            public UnmodifiableIterator<Map.Entry<V, K>> iterator() {
                return this.asList().iterator();
            }

            @Override
            public void forEach(Consumer<? super Map.Entry<V, K>> action) {
                this.asList().forEach(action);
            }

            @Override
            ImmutableList<Map.Entry<V, K>> createAsList() {
                return new ImmutableAsList<Map.Entry<V, K>>(){

                    @Override
                    public Map.Entry<V, K> get(int index) {
                        Map.Entry entry = RegularImmutableBiMap.this.entries[index];
                        return Maps.immutableEntry(entry.getValue(), entry.getKey());
                    }

                    @Override
                    ImmutableCollection<Map.Entry<V, K>> delegateCollection() {
                        return InverseEntrySet.this;
                    }
                };
            }
        }
    }
}

