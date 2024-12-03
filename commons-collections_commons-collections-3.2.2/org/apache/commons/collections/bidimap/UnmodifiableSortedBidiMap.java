/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.SortedBidiMap;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.bidimap.AbstractSortedBidiMapDecorator;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections.map.UnmodifiableEntrySet;
import org.apache.commons.collections.map.UnmodifiableSortedMap;
import org.apache.commons.collections.set.UnmodifiableSet;

public final class UnmodifiableSortedBidiMap
extends AbstractSortedBidiMapDecorator
implements Unmodifiable {
    private UnmodifiableSortedBidiMap inverse;

    public static SortedBidiMap decorate(SortedBidiMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableSortedBidiMap(map);
    }

    private UnmodifiableSortedBidiMap(SortedBidiMap map) {
        super(map);
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map mapToCopy) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        Set set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }

    public Set keySet() {
        Set set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection values() {
        Collection coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }

    public Object removeValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public MapIterator mapIterator() {
        return this.orderedMapIterator();
    }

    public BidiMap inverseBidiMap() {
        return this.inverseSortedBidiMap();
    }

    public OrderedMapIterator orderedMapIterator() {
        OrderedMapIterator it = this.getSortedBidiMap().orderedMapIterator();
        return UnmodifiableOrderedMapIterator.decorate(it);
    }

    public OrderedBidiMap inverseOrderedBidiMap() {
        return this.inverseSortedBidiMap();
    }

    public SortedBidiMap inverseSortedBidiMap() {
        if (this.inverse == null) {
            this.inverse = new UnmodifiableSortedBidiMap(this.getSortedBidiMap().inverseSortedBidiMap());
            this.inverse.inverse = this;
        }
        return this.inverse;
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        SortedMap sm = this.getSortedBidiMap().subMap(fromKey, toKey);
        return UnmodifiableSortedMap.decorate(sm);
    }

    public SortedMap headMap(Object toKey) {
        SortedMap sm = this.getSortedBidiMap().headMap(toKey);
        return UnmodifiableSortedMap.decorate(sm);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap sm = this.getSortedBidiMap().tailMap(fromKey);
        return UnmodifiableSortedMap.decorate(sm);
    }
}

