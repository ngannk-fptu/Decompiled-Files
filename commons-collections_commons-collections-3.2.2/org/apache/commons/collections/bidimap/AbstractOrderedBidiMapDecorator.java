/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.bidimap.AbstractBidiMapDecorator;

public abstract class AbstractOrderedBidiMapDecorator
extends AbstractBidiMapDecorator
implements OrderedBidiMap {
    protected AbstractOrderedBidiMapDecorator(OrderedBidiMap map) {
        super(map);
    }

    protected OrderedBidiMap getOrderedBidiMap() {
        return (OrderedBidiMap)this.map;
    }

    public OrderedMapIterator orderedMapIterator() {
        return this.getOrderedBidiMap().orderedMapIterator();
    }

    public Object firstKey() {
        return this.getOrderedBidiMap().firstKey();
    }

    public Object lastKey() {
        return this.getOrderedBidiMap().lastKey();
    }

    public Object nextKey(Object key) {
        return this.getOrderedBidiMap().nextKey(key);
    }

    public Object previousKey(Object key) {
        return this.getOrderedBidiMap().previousKey(key);
    }

    public OrderedBidiMap inverseOrderedBidiMap() {
        return this.getOrderedBidiMap().inverseOrderedBidiMap();
    }
}

