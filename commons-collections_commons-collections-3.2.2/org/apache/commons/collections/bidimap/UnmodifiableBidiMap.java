/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.bidimap.AbstractBidiMapDecorator;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections.map.UnmodifiableEntrySet;
import org.apache.commons.collections.set.UnmodifiableSet;

public final class UnmodifiableBidiMap
extends AbstractBidiMapDecorator
implements Unmodifiable {
    private UnmodifiableBidiMap inverse;

    public static BidiMap decorate(BidiMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableBidiMap(map);
    }

    private UnmodifiableBidiMap(BidiMap map) {
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
        MapIterator it = this.getBidiMap().mapIterator();
        return UnmodifiableMapIterator.decorate(it);
    }

    public BidiMap inverseBidiMap() {
        if (this.inverse == null) {
            this.inverse = new UnmodifiableBidiMap(this.getBidiMap().inverseBidiMap());
            this.inverse.inverse = this;
        }
        return this.inverse;
    }
}

