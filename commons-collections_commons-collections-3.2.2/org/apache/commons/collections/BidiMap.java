/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;

public interface BidiMap
extends IterableMap {
    public MapIterator mapIterator();

    public Object put(Object var1, Object var2);

    public Object getKey(Object var1);

    public Object removeValue(Object var1);

    public BidiMap inverseBidiMap();
}

