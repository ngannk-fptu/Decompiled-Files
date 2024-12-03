/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.DoubleEndedHeap;

public interface DoubleEndedValueHeap<K, V>
extends DoubleEndedHeap<K> {
    public void insert(K var1, V var2);

    public V findMinValue();

    public V findMaxValue();
}

