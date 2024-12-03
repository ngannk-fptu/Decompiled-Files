/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$AbstractMapEntry;
import com.google.inject.internal.util.$Nullable;
import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class $ImmutableEntry<K, V>
extends $AbstractMapEntry<K, V>
implements Serializable {
    private final K key;
    private final V value;
    private static final long serialVersionUID = 0L;

    $ImmutableEntry(@$Nullable K key, @$Nullable V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }
}

