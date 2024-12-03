/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class NullMap<K, V>
implements Map<K, V>,
Serializable {
    @Override
    public final int size() {
        return 0;
    }

    @Override
    public final void clear() {
    }

    @Override
    public final boolean isEmpty() {
        return true;
    }

    @Override
    public final boolean containsKey(Object object) {
        return false;
    }

    @Override
    public final boolean containsValue(Object object) {
        return false;
    }

    @Override
    public final Collection<V> values() {
        return Collections.emptyList();
    }

    @Override
    public final void putAll(Map map) {
    }

    @Override
    public final Set<Map.Entry<K, V>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public final Set<K> keySet() {
        return Collections.emptySet();
    }

    @Override
    public final V get(Object object) {
        return null;
    }

    @Override
    public final V remove(Object object) {
        return null;
    }

    @Override
    public final V put(Object object, Object object2) {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Map && ((Map)object).isEmpty();
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

