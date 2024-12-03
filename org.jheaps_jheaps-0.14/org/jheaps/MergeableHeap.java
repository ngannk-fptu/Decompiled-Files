/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.Heap;

public interface MergeableHeap<K>
extends Heap<K> {
    public void meld(MergeableHeap<K> var1);
}

