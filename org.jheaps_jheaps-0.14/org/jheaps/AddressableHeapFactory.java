/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import java.util.Comparator;
import org.jheaps.AddressableHeap;

public interface AddressableHeapFactory<K, V> {
    public AddressableHeap<K, V> get(Comparator<? super K> var1);
}

