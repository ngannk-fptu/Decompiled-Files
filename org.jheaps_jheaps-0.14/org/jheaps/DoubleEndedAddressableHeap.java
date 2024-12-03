/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import org.jheaps.AddressableHeap;

public interface DoubleEndedAddressableHeap<K, V>
extends AddressableHeap<K, V> {
    @Override
    public Handle<K, V> insert(K var1, V var2);

    @Override
    public Handle<K, V> insert(K var1);

    @Override
    public Handle<K, V> findMin();

    @Override
    public Handle<K, V> deleteMin();

    public Handle<K, V> findMax();

    public Handle<K, V> deleteMax();

    public static interface Handle<K, V>
    extends AddressableHeap.Handle<K, V> {
        public void increaseKey(K var1);
    }
}

