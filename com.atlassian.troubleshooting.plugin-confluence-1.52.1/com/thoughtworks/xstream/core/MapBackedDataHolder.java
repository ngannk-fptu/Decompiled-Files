/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.DataHolder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapBackedDataHolder
implements DataHolder {
    private final Map map;

    public MapBackedDataHolder() {
        this(new HashMap());
    }

    public MapBackedDataHolder(Map map) {
        this.map = map;
    }

    public Object get(Object key) {
        return this.map.get(key);
    }

    public void put(Object key, Object value) {
        this.map.put(key, value);
    }

    public Iterator keys() {
        return Collections.unmodifiableCollection(this.map.keySet()).iterator();
    }
}

