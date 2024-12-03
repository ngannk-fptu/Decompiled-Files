/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.AbstractDecoratedMap;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public class IgnoreCaseMap<V>
extends AbstractDecoratedMap<String, V>
implements Serializable,
Cloneable {
    public IgnoreCaseMap() {
    }

    public IgnoreCaseMap(Map<String, ? extends V> map) {
        super(map);
    }

    public IgnoreCaseMap(Map map, Map<String, ? extends V> map2) {
        super(map, map2);
    }

    @Override
    public V put(String string, V v) {
        String string2 = (String)IgnoreCaseMap.toUpper(string);
        return this.unwrap((Map.Entry)this.entries.put(string2, new AbstractDecoratedMap.BasicEntry<String, V>(string2, v)));
    }

    private V unwrap(Map.Entry<String, V> entry) {
        return entry != null ? (V)entry.getValue() : null;
    }

    @Override
    public V get(Object object) {
        return this.unwrap((Map.Entry)this.entries.get(IgnoreCaseMap.toUpper(object)));
    }

    @Override
    public V remove(Object object) {
        return this.unwrap((Map.Entry)this.entries.remove(IgnoreCaseMap.toUpper(object)));
    }

    @Override
    public boolean containsKey(Object object) {
        return this.entries.containsKey(IgnoreCaseMap.toUpper(object));
    }

    protected static Object toUpper(Object object) {
        if (object instanceof String) {
            return ((String)object).toUpperCase();
        }
        return object;
    }

    @Override
    protected Iterator<Map.Entry<String, V>> newEntryIterator() {
        return this.entries.entrySet().iterator();
    }

    @Override
    protected Iterator<String> newKeyIterator() {
        return this.entries.keySet().iterator();
    }

    @Override
    protected Iterator<V> newValueIterator() {
        return this.entries.values().iterator();
    }
}

