/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import java.util.Comparator;

public interface Heap<K> {
    public Comparator<? super K> comparator();

    public void insert(K var1);

    public K findMin();

    public K deleteMin();

    public boolean isEmpty();

    public long size();

    public void clear();
}

