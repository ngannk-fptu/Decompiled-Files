/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.AddressableHeap;

public interface MergeableAddressableHeap<K, V>
extends AddressableHeap<K, V> {
    public void meld(MergeableAddressableHeap<K, V> var1);
}

