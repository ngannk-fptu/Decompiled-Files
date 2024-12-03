/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

public class LazySortedMap
extends LazyMap
implements SortedMap {
    private static final long serialVersionUID = 2715322183617658933L;

    public static SortedMap decorate(SortedMap map, Factory factory) {
        return new LazySortedMap(map, factory);
    }

    public static SortedMap decorate(SortedMap map, Transformer factory) {
        return new LazySortedMap(map, factory);
    }

    protected LazySortedMap(SortedMap map, Factory factory) {
        super((Map)map, factory);
    }

    protected LazySortedMap(SortedMap map, Transformer factory) {
        super((Map)map, factory);
    }

    protected SortedMap getSortedMap() {
        return (SortedMap)this.map;
    }

    public Object firstKey() {
        return this.getSortedMap().firstKey();
    }

    public Object lastKey() {
        return this.getSortedMap().lastKey();
    }

    public Comparator comparator() {
        return this.getSortedMap().comparator();
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        SortedMap map = this.getSortedMap().subMap(fromKey, toKey);
        return new LazySortedMap(map, this.factory);
    }

    public SortedMap headMap(Object toKey) {
        SortedMap map = this.getSortedMap().headMap(toKey);
        return new LazySortedMap(map, this.factory);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap map = this.getSortedMap().tailMap(fromKey);
        return new LazySortedMap(map, this.factory);
    }
}

