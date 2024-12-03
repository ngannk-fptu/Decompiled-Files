/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.Mutable
 */
package com.atlassian.confluence.cluster.shareddata;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.mutable.Mutable;

@Deprecated(since="8.2", forRemoval=true)
public class SharedDataMutable<K extends Serializable, V extends Serializable>
implements Mutable<V> {
    private final Map<K, V> map;
    private final K key;
    private final V defaultValue;

    public SharedDataMutable(Map<K, V> map, K key, V defaultValue) {
        this.map = map;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public V getValue() {
        return (V)((Serializable)this.map.getOrDefault(this.key, this.defaultValue));
    }

    public void setValue(V value) {
        this.map.put(this.key, value);
    }
}

