/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.function.Function;
import org.hibernate.LockMode;
import org.hibernate.internal.util.collections.LazyIndexedMap;

public final class LockModeEnumMap<V>
extends LazyIndexedMap<LockMode, V> {
    private static final int ENUM_DIMENSION = LockMode.values().length;

    public LockModeEnumMap() {
        super(ENUM_DIMENSION);
    }

    public V computeIfAbsent(LockMode key, Function<LockMode, V> valueGenerator) {
        return super.computeIfAbsent(key.ordinal(), key, valueGenerator);
    }
}

