/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections4;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.OrderedMap;

public interface OrderedBidiMap<K, V>
extends BidiMap<K, V>,
OrderedMap<K, V> {
    @Override
    public OrderedBidiMap<V, K> inverseBidiMap();
}

