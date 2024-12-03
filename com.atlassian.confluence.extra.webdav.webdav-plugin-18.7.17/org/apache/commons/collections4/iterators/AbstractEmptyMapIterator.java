/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.iterators.AbstractEmptyIterator;

public abstract class AbstractEmptyMapIterator<K, V>
extends AbstractEmptyIterator<K> {
    public K getKey() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public V getValue() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public V setValue(V value) {
        throw new IllegalStateException("Iterator contains no elements");
    }
}

