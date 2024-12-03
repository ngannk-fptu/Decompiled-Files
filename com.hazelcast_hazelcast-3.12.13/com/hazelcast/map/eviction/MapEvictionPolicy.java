/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.eviction;

import com.hazelcast.core.EntryView;
import java.util.Comparator;

public abstract class MapEvictionPolicy<K, V>
implements Comparator<EntryView<K, V>> {
    @Override
    public abstract int compare(EntryView<K, V> var1, EntryView<K, V> var2);
}

