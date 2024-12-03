/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.DoubleEndedAddressableHeap;

public interface MergeableDoubleEndedAddressableHeap<K, V>
extends DoubleEndedAddressableHeap<K, V> {
    public void meld(MergeableDoubleEndedAddressableHeap<K, V> var1);
}

