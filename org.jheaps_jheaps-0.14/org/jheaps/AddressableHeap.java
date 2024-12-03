/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps;

import java.util.Comparator;

public interface AddressableHeap<K, V> {
    public Comparator<? super K> comparator();

    public Handle<K, V> insert(K var1, V var2);

    public Handle<K, V> insert(K var1);

    public Handle<K, V> findMin();

    public Handle<K, V> deleteMin();

    public boolean isEmpty();

    public long size();

    public void clear();

    public static interface Handle<K, V> {
        public K getKey();

        public V getValue();

        public void setValue(V var1);

        public void decreaseKey(K var1);

        public void delete();
    }
}

