/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections.SortedBidiMap;
import org.apache.commons.collections.bidimap.AbstractOrderedBidiMapDecorator;

public abstract class AbstractSortedBidiMapDecorator
extends AbstractOrderedBidiMapDecorator
implements SortedBidiMap {
    public AbstractSortedBidiMapDecorator(SortedBidiMap map) {
        super(map);
    }

    protected SortedBidiMap getSortedBidiMap() {
        return (SortedBidiMap)this.map;
    }

    public SortedBidiMap inverseSortedBidiMap() {
        return this.getSortedBidiMap().inverseSortedBidiMap();
    }

    public Comparator comparator() {
        return this.getSortedBidiMap().comparator();
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        return this.getSortedBidiMap().subMap(fromKey, toKey);
    }

    public SortedMap headMap(Object toKey) {
        return this.getSortedBidiMap().headMap(toKey);
    }

    public SortedMap tailMap(Object fromKey) {
        return this.getSortedBidiMap().tailMap(fromKey);
    }
}

