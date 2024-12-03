/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.collect;

import brave.internal.collect.LongBitSet;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class UnsafeArrayMap<K, V>
implements Map<K, V> {
    static final int MAX_FILTERED_KEYS = 64;
    final Object[] array;
    final int toIndex;
    final int size;
    final long filteredBitSet;

    public static <K, V> Builder<K, V> newBuilder() {
        return new Builder();
    }

    UnsafeArrayMap(Object[] array, int toIndex, long filteredBitSet) {
        this.array = array;
        this.toIndex = toIndex;
        this.filteredBitSet = filteredBitSet;
        this.size = toIndex / 2 - LongBitSet.size(filteredBitSet);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean containsKey(Object o) {
        if (o == null) {
            return false;
        }
        return this.arrayIndexOfKey(o) != -1;
    }

    @Override
    public boolean containsValue(Object o) {
        for (int i = 0; i < this.toIndex; i += 2) {
            if (UnsafeArrayMap.isFilteredKey(this.filteredBitSet, i) || !this.value(i + 1).equals(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V get(Object o) {
        if (o == null) {
            return null;
        }
        int i = this.arrayIndexOfKey(o);
        return i != -1 ? (V)this.value(i + 1) : null;
    }

    int arrayIndexOfKey(Object o) {
        int result = -1;
        for (int i = 0; i < this.toIndex; i += 2) {
            if (UnsafeArrayMap.isFilteredKey(this.filteredBitSet, i) || !o.equals(this.key(i))) continue;
            return i;
        }
        return result;
    }

    K key(int i) {
        return (K)this.array[i];
    }

    V value(int i) {
        return (V)this.array[i];
    }

    @Override
    public Set<K> keySet() {
        return new KeySetView();
    }

    @Override
    public Collection<V> values() {
        return new ValuesView();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySetView();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.toIndex; i += 2) {
            if (UnsafeArrayMap.isFilteredKey(this.filteredBitSet, i)) continue;
            if (result.length() > 0) {
                result.append(',');
            }
            result.append(this.key(i)).append('=').append(this.value(i + 1));
        }
        return result.insert(0, "UnsafeArrayMap{").append("}").toString();
    }

    static long setFilteredKey(long filteredKeys, int i) {
        return LongBitSet.setBit(filteredKeys, i / 2);
    }

    static boolean isFilteredKey(long filteredKeys, int i) {
        return LongBitSet.isSet(filteredKeys, i / 2);
    }

    abstract class SetView<E>
    implements Set<E> {
        SetView() {
        }

        int advancePastFiltered(int i) {
            while (i < UnsafeArrayMap.this.toIndex && UnsafeArrayMap.isFilteredKey(UnsafeArrayMap.this.filteredBitSet, i)) {
                i += 2;
            }
            return i;
        }

        @Override
        public int size() {
            return UnsafeArrayMap.this.size;
        }

        abstract E elementAtArrayIndex(int var1);

        @Override
        public Iterator<E> iterator() {
            return new ReadOnlyIterator();
        }

        @Override
        public Object[] toArray() {
            return this.copyTo(new Object[UnsafeArrayMap.this.size]);
        }

        @Override
        public <T> T[] toArray(T[] a) {
            T[] result = a.length >= UnsafeArrayMap.this.size ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), this.size());
            return this.copyTo(result);
        }

        <T> T[] copyTo(T[] dest) {
            int d = 0;
            for (int i = 0; i < UnsafeArrayMap.this.toIndex; i += 2) {
                if (UnsafeArrayMap.isFilteredKey(UnsafeArrayMap.this.filteredBitSet, i)) continue;
                dest[d++] = this.elementAtArrayIndex(i);
            }
            return dest;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            if (c == null) {
                return false;
            }
            if (c.isEmpty()) {
                return true;
            }
            for (Object element : c) {
                if (this.contains(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        final class ReadOnlyIterator
        implements Iterator<E> {
            int i;

            ReadOnlyIterator() {
                this.i = SetView.this.advancePastFiltered(0);
            }

            @Override
            public boolean hasNext() {
                this.i = SetView.this.advancePastFiltered(this.i);
                return this.i < UnsafeArrayMap.this.toIndex;
            }

            @Override
            public E next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                Object result = SetView.this.elementAtArrayIndex(this.i);
                this.i += 2;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    final class EntrySetView
    extends SetView<Map.Entry<K, V>> {
        EntrySetView() {
        }

        @Override
        Map.Entry<K, V> elementAtArrayIndex(int i) {
            return new AbstractMap.SimpleImmutableEntry(UnsafeArrayMap.this.key(i), UnsafeArrayMap.this.value(i + 1));
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry) || ((Map.Entry)o).getKey() == null) {
                return false;
            }
            Map.Entry that = (Map.Entry)o;
            int i = UnsafeArrayMap.this.arrayIndexOfKey(that.getKey());
            if (i == -1) {
                return false;
            }
            return UnsafeArrayMap.this.value(i + 1).equals(that.getValue());
        }
    }

    final class ValuesView
    extends SetView<V> {
        ValuesView() {
        }

        @Override
        V elementAtArrayIndex(int i) {
            return UnsafeArrayMap.this.value(i + 1);
        }

        @Override
        public boolean contains(Object o) {
            return UnsafeArrayMap.this.containsValue(o);
        }
    }

    final class KeySetView
    extends SetView<K> {
        KeySetView() {
        }

        @Override
        K elementAtArrayIndex(int i) {
            return UnsafeArrayMap.this.key(i);
        }

        @Override
        public boolean contains(Object o) {
            return UnsafeArrayMap.this.containsKey(o);
        }
    }

    static final class KeyMapping<K, V>
    extends UnsafeArrayMap<K, V> {
        final Mapper<Object, K> keyMapper;

        KeyMapping(Builder builder, Object[] array, int toIndex, long filteredBitSet) {
            super(array, toIndex, filteredBitSet);
            this.keyMapper = builder.keyMapper;
        }

        @Override
        K key(int i) {
            return this.keyMapper.map(this.array[i]);
        }
    }

    public static final class Builder<K, V> {
        Mapper<Object, K> keyMapper;
        K[] filteredKeys = new Object[0];

        public Builder<K, V> mapKeys(Mapper<Object, K> keyMapper) {
            if (keyMapper == null) {
                throw new NullPointerException("keyMapper == null");
            }
            this.keyMapper = keyMapper;
            return this;
        }

        public Builder<K, V> filterKeys(K ... filteredKeys) {
            if (filteredKeys == null) {
                throw new NullPointerException("filteredKeys == null");
            }
            if (filteredKeys.length > 64) {
                throw new IllegalArgumentException("cannot filter more than 64 keys");
            }
            this.filteredKeys = filteredKeys;
            return this;
        }

        public Map<K, V> build(Object[] array) {
            int i;
            if (array == null) {
                throw new NullPointerException("array == null");
            }
            long filteredBitSet = 0L;
            int numFiltered = 0;
            block0: for (i = 0; i < array.length && array[i] != null; i += 2) {
                if (array[i + 1] == null) {
                    filteredBitSet = UnsafeArrayMap.setFilteredKey(filteredBitSet, i);
                    ++numFiltered;
                    continue;
                }
                Object key = this.keyMapper != null ? this.keyMapper.map(array[i]) : array[i];
                for (K filteredKey : this.filteredKeys) {
                    if (!filteredKey.equals(key)) continue;
                    filteredBitSet = UnsafeArrayMap.setFilteredKey(filteredBitSet, i);
                    ++numFiltered;
                    continue block0;
                }
            }
            if (numFiltered == i / 2) {
                return Collections.emptyMap();
            }
            if (this.keyMapper == null) {
                return new UnsafeArrayMap(array, i, filteredBitSet);
            }
            return new KeyMapping(this, array, i, filteredBitSet);
        }
    }

    public static interface Mapper<V1, V2> {
        public V2 map(V1 var1);
    }
}

