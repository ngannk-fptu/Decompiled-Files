/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class MapWithDefault<K, V>
implements Map<K, V> {
    private final Map<K, V> delegate;
    private final Closure initClosure;

    private MapWithDefault(Map<K, V> m, Closure initClosure) {
        this.delegate = m;
        this.initClosure = initClosure;
    }

    public static <K, V> Map<K, V> newInstance(Map<K, V> m, Closure initClosure) {
        return new MapWithDefault<K, V>(m, initClosure);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (!this.delegate.containsKey(key)) {
            this.delegate.put(key, this.initClosure.call(new Object[]{key}));
        }
        return this.delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.delegate.putAll(m);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.delegate.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.delegate.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
}

