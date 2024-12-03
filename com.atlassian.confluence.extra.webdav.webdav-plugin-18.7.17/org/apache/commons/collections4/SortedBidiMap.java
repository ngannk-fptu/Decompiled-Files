/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections4;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedBidiMap;

public interface SortedBidiMap<K, V>
extends OrderedBidiMap<K, V>,
SortedMap<K, V> {
    @Override
    public SortedBidiMap<V, K> inverseBidiMap();

    public Comparator<? super V> valueComparator();
}

