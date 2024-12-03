/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.Heap;

public interface ValueHeap<K, V>
extends Heap<K> {
    public void insert(K var1, V var2);

    public V findMinValue();
}

