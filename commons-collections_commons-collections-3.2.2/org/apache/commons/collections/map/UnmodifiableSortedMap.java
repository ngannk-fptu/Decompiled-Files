/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.map.AbstractSortedMapDecorator;
import org.apache.commons.collections.map.UnmodifiableEntrySet;
import org.apache.commons.collections.set.UnmodifiableSet;

public final class UnmodifiableSortedMap
extends AbstractSortedMapDecorator
implements Unmodifiable,
Serializable {
    private static final long serialVersionUID = 5805344239827376360L;

    public static SortedMap decorate(SortedMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableSortedMap(map);
    }

    private UnmodifiableSortedMap(SortedMap map) {
        super(map);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
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
        return new UnmodifiableSortedMap(map);
    }

    public SortedMap headMap(Object toKey) {
        SortedMap map = this.getSortedMap().headMap(toKey);
        return new UnmodifiableSortedMap(map);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap map = this.getSortedMap().tailMap(fromKey);
        return new UnmodifiableSortedMap(map);
    }
}

