/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.map.AbstractMapDecorator;

public abstract class AbstractOrderedMapDecorator
extends AbstractMapDecorator
implements OrderedMap {
    protected AbstractOrderedMapDecorator() {
    }

    public AbstractOrderedMapDecorator(OrderedMap map) {
        super(map);
    }

    protected OrderedMap getOrderedMap() {
        return (OrderedMap)this.map;
    }

    public Object firstKey() {
        return this.getOrderedMap().firstKey();
    }

    public Object lastKey() {
        return this.getOrderedMap().lastKey();
    }

    public Object nextKey(Object key) {
        return this.getOrderedMap().nextKey(key);
    }

    public Object previousKey(Object key) {
        return this.getOrderedMap().previousKey(key);
    }

    public MapIterator mapIterator() {
        return this.getOrderedMap().mapIterator();
    }

    public OrderedMapIterator orderedMapIterator() {
        return this.getOrderedMap().orderedMapIterator();
    }
}

