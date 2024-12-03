/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;

@SdkProtectedApi
public final class DefaultSdkAutoConstructMap<K, V>
implements SdkAutoConstructMap<K, V> {
    private static final DefaultSdkAutoConstructMap INSTANCE = new DefaultSdkAutoConstructMap();
    private final Map<K, V> impl = Collections.emptyMap();

    private DefaultSdkAutoConstructMap() {
    }

    public static <K, V> DefaultSdkAutoConstructMap<K, V> getInstance() {
        return INSTANCE;
    }

    @Override
    public int size() {
        return this.impl.size();
    }

    @Override
    public boolean isEmpty() {
        return this.impl.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.impl.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.impl.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.impl.get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.impl.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.impl.get(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.impl.putAll(m);
    }

    @Override
    public void clear() {
        this.impl.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.impl.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.impl.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.impl.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.impl.equals(o);
    }

    @Override
    public int hashCode() {
        return this.impl.hashCode();
    }

    public String toString() {
        return this.impl.toString();
    }
}

