/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$AbstractIterator;
import com.google.inject.internal.util.$Hashing;
import com.google.inject.internal.util.$ImmutableCollection;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Iterables;
import com.google.inject.internal.util.$Iterators;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$UnmodifiableIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $ImmutableMap<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    private static final $ImmutableMap<?, ?> EMPTY_IMMUTABLE_MAP = new EmptyImmutableMap();

    public static <K, V> $ImmutableMap<K, V> of() {
        return EMPTY_IMMUTABLE_MAP;
    }

    public static <K, V> $ImmutableMap<K, V> of(K k1, V v1) {
        return new SingletonImmutableMap($Preconditions.checkNotNull(k1), $Preconditions.checkNotNull(v1));
    }

    public static <K, V> $ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
        return new RegularImmutableMap(new Map.Entry[]{$ImmutableMap.entryOf(k1, v1), $ImmutableMap.entryOf(k2, v2)});
    }

    public static <K, V> $ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return new RegularImmutableMap(new Map.Entry[]{$ImmutableMap.entryOf(k1, v1), $ImmutableMap.entryOf(k2, v2), $ImmutableMap.entryOf(k3, v3)});
    }

    public static <K, V> $ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new RegularImmutableMap(new Map.Entry[]{$ImmutableMap.entryOf(k1, v1), $ImmutableMap.entryOf(k2, v2), $ImmutableMap.entryOf(k3, v3), $ImmutableMap.entryOf(k4, v4)});
    }

    public static <K, V> $ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return new RegularImmutableMap(new Map.Entry[]{$ImmutableMap.entryOf(k1, v1), $ImmutableMap.entryOf(k2, v2), $ImmutableMap.entryOf(k3, v3), $ImmutableMap.entryOf(k4, v4), $ImmutableMap.entryOf(k5, v5)});
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder();
    }

    private static <K, V> Map.Entry<K, V> entryOf(K key, V value) {
        return $Maps.immutableEntry($Preconditions.checkNotNull(key), $Preconditions.checkNotNull(value));
    }

    public static <K, V> $ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        if (map instanceof $ImmutableMap) {
            $ImmutableMap kvMap = ($ImmutableMap)map;
            return kvMap;
        }
        int size = map.size();
        switch (size) {
            case 0: {
                return $ImmutableMap.of();
            }
            case 1: {
                Map.Entry<K, V> loneEntry = $Iterables.getOnlyElement(map.entrySet());
                return $ImmutableMap.of(loneEntry.getKey(), loneEntry.getValue());
            }
        }
        Map.Entry[] array = new Map.Entry[size];
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            array[i++] = $ImmutableMap.entryOf(entry.getKey(), entry.getValue());
        }
        return new RegularImmutableMap(array);
    }

    $ImmutableMap() {
    }

    @Override
    public final V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final V remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract boolean containsKey(@$Nullable Object var1);

    @Override
    public abstract boolean containsValue(@$Nullable Object var1);

    @Override
    public abstract V get(@$Nullable Object var1);

    @Override
    public abstract $ImmutableSet<Map.Entry<K, V>> entrySet();

    @Override
    public abstract $ImmutableSet<K> keySet();

    @Override
    public abstract $ImmutableCollection<V> values();

    @Override
    public boolean equals(@$Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Map) {
            Map that = (Map)object;
            return (($ImmutableSet)this.entrySet()).equals(that.entrySet());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (($ImmutableSet)this.entrySet()).hashCode();
    }

    public String toString() {
        StringBuilder result = new StringBuilder(this.size() * 16).append('{');
        Iterator entries = (($ImmutableSet)this.entrySet()).iterator();
        result.append(entries.next());
        while (entries.hasNext()) {
            result.append(", ").append(entries.next());
        }
        return result.append('}').toString();
    }

    Object writeReplace() {
        return new SerializedForm(this);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SerializedForm
    implements Serializable {
        final Object[] keys;
        final Object[] values;
        private static final long serialVersionUID = 0L;

        SerializedForm($ImmutableMap<?, ?> map) {
            this.keys = new Object[map.size()];
            this.values = new Object[map.size()];
            int i = 0;
            for (Map.Entry entry : map.entrySet()) {
                this.keys[i] = entry.getKey();
                this.values[i] = entry.getValue();
                ++i;
            }
        }

        Object readResolve() {
            Builder<Object, Object> builder = new Builder<Object, Object>();
            for (int i = 0; i < this.keys.length; ++i) {
                builder.put(this.keys[i], this.values[i]);
            }
            return builder.build();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class RegularImmutableMap<K, V>
    extends $ImmutableMap<K, V> {
        private final transient Map.Entry<K, V>[] entries;
        private final transient Object[] table;
        private final transient int mask;
        private final transient int keySetHashCode;
        private transient $ImmutableSet<Map.Entry<K, V>> entrySet;
        private transient $ImmutableSet<K> keySet;
        private transient $ImmutableCollection<V> values;

        private RegularImmutableMap(Map.Entry<?, ?> ... entries) {
            Map.Entry<?, ?>[] tmp = entries;
            this.entries = tmp;
            int tableSize = $Hashing.chooseTableSize(entries.length);
            this.table = new Object[tableSize * 2];
            this.mask = tableSize - 1;
            int keySetHashCodeMutable = 0;
            block0: for (Map.Entry<K, V> entry : this.entries) {
                K key = entry.getKey();
                int keyHashCode = key.hashCode();
                int i = $Hashing.smear(keyHashCode);
                while (true) {
                    int index;
                    Object existing;
                    if ((existing = this.table[index = (i & this.mask) * 2]) == null) {
                        V value = entry.getValue();
                        this.table[index] = key;
                        this.table[index + 1] = value;
                        keySetHashCodeMutable += keyHashCode;
                        continue block0;
                    }
                    if (existing.equals(key)) {
                        throw new IllegalArgumentException("duplicate key: " + key);
                    }
                    ++i;
                }
            }
            this.keySetHashCode = keySetHashCodeMutable;
        }

        @Override
        public V get(Object key) {
            if (key == null) {
                return null;
            }
            int i = $Hashing.smear(key.hashCode());
            int index;
            Object candidate;
            while ((candidate = this.table[index = (i & this.mask) * 2]) != null) {
                if (candidate.equals(key)) {
                    Object value = this.table[index + 1];
                    return (V)value;
                }
                ++i;
            }
            return null;
        }

        @Override
        public int size() {
            return this.entries.length;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.get(key) != null;
        }

        @Override
        public boolean containsValue(Object value) {
            if (value == null) {
                return false;
            }
            for (Map.Entry<K, V> entry : this.entries) {
                if (!entry.getValue().equals(value)) continue;
                return true;
            }
            return false;
        }

        @Override
        public $ImmutableSet<Map.Entry<K, V>> entrySet() {
            $ImmutableSet<Map.Entry<K, V>> es = this.entrySet;
            return es == null ? (this.entrySet = new EntrySet(this)) : es;
        }

        @Override
        public $ImmutableSet<K> keySet() {
            $ImmutableSet<K> ks = this.keySet;
            return ks == null ? (this.keySet = new KeySet(this)) : ks;
        }

        @Override
        public $ImmutableCollection<V> values() {
            $ImmutableCollection<V> v = this.values;
            return v == null ? (this.values = new Values(this)) : v;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(this.size() * 16).append('{').append(this.entries[0]);
            for (int e = 1; e < this.entries.length; ++e) {
                result.append(", ").append(this.entries[e].toString());
            }
            return result.append('}').toString();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class Values<V>
        extends $ImmutableCollection<V> {
            final RegularImmutableMap<?, V> map;

            Values(RegularImmutableMap<?, V> map) {
                this.map = map;
            }

            @Override
            public int size() {
                return ((RegularImmutableMap)this.map).entries.length;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public $UnmodifiableIterator<V> iterator() {
                $AbstractIterator iterator = new $AbstractIterator<V>(){
                    int index = 0;

                    @Override
                    protected V computeNext() {
                        return this.index < Values.this.map.entries.length ? Values.this.map.entries[this.index++].getValue() : this.endOfData();
                    }
                };
                return $Iterators.unmodifiableIterator(iterator);
            }

            @Override
            public boolean contains(Object target) {
                return this.map.containsValue(target);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class KeySet<K, V>
        extends $ImmutableSet.TransformedImmutableSet<Map.Entry<K, V>, K> {
            final RegularImmutableMap<K, V> map;

            KeySet(RegularImmutableMap<K, V> map) {
                super(((RegularImmutableMap)map).entries, ((RegularImmutableMap)map).keySetHashCode);
                this.map = map;
            }

            @Override
            K transform(Map.Entry<K, V> element) {
                return element.getKey();
            }

            @Override
            public boolean contains(Object target) {
                return this.map.containsKey(target);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class EntrySet<K, V>
        extends $ImmutableSet.ArrayImmutableSet<Map.Entry<K, V>> {
            final RegularImmutableMap<K, V> map;

            EntrySet(RegularImmutableMap<K, V> map) {
                super(((RegularImmutableMap)map).entries);
                this.map = map;
            }

            @Override
            public boolean contains(Object target) {
                if (target instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry)target;
                    V mappedValue = this.map.get(entry.getKey());
                    return mappedValue != null && mappedValue.equals(entry.getValue());
                }
                return false;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class SingletonImmutableMap<K, V>
    extends $ImmutableMap<K, V> {
        private final transient K singleKey;
        private final transient V singleValue;
        private transient Map.Entry<K, V> entry;
        private transient $ImmutableSet<Map.Entry<K, V>> entrySet;
        private transient $ImmutableSet<K> keySet;
        private transient $ImmutableCollection<V> values;

        private SingletonImmutableMap(K singleKey, V singleValue) {
            this.singleKey = singleKey;
            this.singleValue = singleValue;
        }

        private SingletonImmutableMap(Map.Entry<K, V> entry) {
            this.entry = entry;
            this.singleKey = entry.getKey();
            this.singleValue = entry.getValue();
        }

        private Map.Entry<K, V> entry() {
            Map.Entry<K, V> e = this.entry;
            return e == null ? (this.entry = $Maps.immutableEntry(this.singleKey, this.singleValue)) : e;
        }

        @Override
        public V get(Object key) {
            return this.singleKey.equals(key) ? (V)this.singleValue : null;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.singleKey.equals(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.singleValue.equals(value);
        }

        @Override
        public $ImmutableSet<Map.Entry<K, V>> entrySet() {
            $ImmutableSet<Map.Entry<K, V>> es = this.entrySet;
            return es == null ? (this.entrySet = $ImmutableSet.of(this.entry())) : es;
        }

        @Override
        public $ImmutableSet<K> keySet() {
            $ImmutableSet<K> ks = this.keySet;
            return ks == null ? (this.keySet = $ImmutableSet.of(this.singleKey)) : ks;
        }

        @Override
        public $ImmutableCollection<V> values() {
            $ImmutableCollection<V> v = this.values;
            return v == null ? (this.values = new Values<V>(this.singleValue)) : v;
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Map) {
                Map that = (Map)object;
                if (that.size() != 1) {
                    return false;
                }
                Map.Entry entry = that.entrySet().iterator().next();
                return this.singleKey.equals(entry.getKey()) && this.singleValue.equals(entry.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.singleKey.hashCode() ^ this.singleValue.hashCode();
        }

        @Override
        public String toString() {
            return '{' + this.singleKey.toString() + '=' + this.singleValue.toString() + '}';
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class Values<V>
        extends $ImmutableCollection<V> {
            final V singleValue;

            Values(V singleValue) {
                this.singleValue = singleValue;
            }

            @Override
            public boolean contains(Object object) {
                return this.singleValue.equals(object);
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public $UnmodifiableIterator<V> iterator() {
                return $Iterators.singletonIterator(this.singleValue);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class EmptyImmutableMap
    extends $ImmutableMap<Object, Object> {
        private EmptyImmutableMap() {
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public $ImmutableSet<Map.Entry<Object, Object>> entrySet() {
            return $ImmutableSet.of();
        }

        @Override
        public $ImmutableSet<Object> keySet() {
            return $ImmutableSet.of();
        }

        @Override
        public $ImmutableCollection<Object> values() {
            return $ImmutableCollection.EMPTY_IMMUTABLE_COLLECTION;
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            if (object instanceof Map) {
                Map that = (Map)object;
                return that.isEmpty();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "{}";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Builder<K, V> {
        final List<Map.Entry<K, V>> entries = $Lists.newArrayList();

        public Builder<K, V> put(K key, V value) {
            this.entries.add($ImmutableMap.entryOf(key, value));
            return this;
        }

        public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public $ImmutableMap<K, V> build() {
            return Builder.fromEntryList(this.entries);
        }

        private static <K, V> $ImmutableMap<K, V> fromEntryList(List<Map.Entry<K, V>> entries) {
            int size = entries.size();
            switch (size) {
                case 0: {
                    return $ImmutableMap.of();
                }
                case 1: {
                    return new SingletonImmutableMap($Iterables.getOnlyElement(entries));
                }
            }
            Map.Entry[] entryArray = entries.toArray(new Map.Entry[entries.size()]);
            return new RegularImmutableMap(entryArray);
        }
    }
}

