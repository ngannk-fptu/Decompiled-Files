/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SetUtil {
    private static final float HASHSET_DEFAULT_LOAD_FACTOR = 0.75f;

    private SetUtil() {
    }

    public static <E> Set<E> createHashSet(int expectedMapSize) {
        int initialCapacity = (int)((float)expectedMapSize / 0.75f) + 1;
        return new HashSet(initialCapacity, 0.75f);
    }

    public static <E> Set<E> createLinkedHashSet(int expectedMapSize) {
        int initialCapacity = (int)((float)expectedMapSize / 0.75f) + 1;
        return new LinkedHashSet(initialCapacity, 0.75f);
    }
}

