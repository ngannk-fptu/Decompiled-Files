/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.OrderedMapIterator;

public interface OrderedMap
extends IterableMap {
    public OrderedMapIterator orderedMapIterator();

    public Object firstKey();

    public Object lastKey();

    public Object nextKey(Object var1);

    public Object previousKey(Object var1);
}

