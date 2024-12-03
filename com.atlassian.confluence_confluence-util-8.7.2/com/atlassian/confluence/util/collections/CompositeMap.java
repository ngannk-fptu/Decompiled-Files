/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.util.collections;

import com.atlassian.confluence.util.collections.LazyMapEntry;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class CompositeMap<K, V>
extends AbstractMap<K, V>
implements Map<K, V> {
    private final Map<K, V> one;
    private final Map<K, V> two;
    private final Removed removed = new Removed();

    public static <K, V> Map<K, V> of(Map<K, V> one, Map<K, V> two) {
        return new CompositeMap<K, V>(one, two);
    }

    CompositeMap(Map<K, V> one, Map<K, V> two) {
        Objects.requireNonNull(one, "First map must not be null");
        Objects.requireNonNull(two, "Second map must not be null");
        this.one = one;
        this.two = two;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Iterable transformMayHaveDuplicates = Iterables.transform(this.keySet(), (Function)new EntryTransformer());
        return ImmutableSet.copyOf((Iterable)transformMayHaveDuplicates);
    }

    @Override
    public V get(Object key) {
        if (this.removed.contains(key)) {
            return null;
        }
        return this.one.containsKey(key) ? this.one.get(key) : this.two.get(key);
    }

    @Override
    public Set<K> keySet() {
        return Sets.filter((Set)Sets.union(this.one.keySet(), this.two.keySet()), key -> !this.removed.contains(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return !this.removed.contains(key) && (this.one.containsKey(key) || this.two.containsKey(key));
    }

    @Override
    public boolean isEmpty() {
        return this.one.isEmpty() && this.two.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V put(K key, V value) {
        try {
            V v = this.one.put(key, value);
            return v;
        }
        finally {
            this.removed.remove(key);
        }
    }

    @Override
    public V remove(Object key) {
        try {
            V v = this.get(key);
            return v;
        }
        finally {
            this.removed.add(key);
        }
    }

    static final class Removed {
        private Set<Object> removed = null;

        Removed() {
        }

        boolean contains(Object key) {
            return this.removed != null && this.removed.contains(key);
        }

        void remove(Object key) {
            if (this.removed != null) {
                this.removed.remove(key);
            }
        }

        void add(Object key) {
            if (this.removed == null) {
                this.removed = Sets.newHashSet((Object[])new Object[]{key});
            } else {
                this.removed.add(key);
            }
        }
    }

    class EntryTransformer
    implements Function<K, Map.Entry<K, V>> {
        EntryTransformer() {
        }

        public Map.Entry<K, V> apply(K key) {
            return new LazyMapEntry(CompositeMap.this, key);
        }
    }
}

