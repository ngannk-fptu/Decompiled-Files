/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.Heap;

public interface DoubleEndedHeap<K>
extends Heap<K> {
    public K findMax();

    public K deleteMax();
}

