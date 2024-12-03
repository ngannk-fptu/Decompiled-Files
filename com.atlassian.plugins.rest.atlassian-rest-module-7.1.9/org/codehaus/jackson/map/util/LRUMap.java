/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LRUMap<K, V>
extends LinkedHashMap<K, V> {
    protected final int _maxEntries;

    public LRUMap(int initialEntries, int maxEntries) {
        super(initialEntries, 0.8f, true);
        this._maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this._maxEntries;
    }
}

