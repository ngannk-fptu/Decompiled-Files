/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true)
class ImmutableEntry<K, V>
extends AbstractMapEntry<K, V>
implements Serializable {
    @ParametricNullness
    final K key;
    @ParametricNullness
    final V value;
    private static final long serialVersionUID = 0L;

    ImmutableEntry(@ParametricNullness K key, @ParametricNullness V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    @ParametricNullness
    public final K getKey() {
        return this.key;
    }

    @Override
    @ParametricNullness
    public final V getValue() {
        return this.value;
    }

    @Override
    @ParametricNullness
    public final V setValue(@ParametricNullness V value) {
        throw new UnsupportedOperationException();
    }
}

