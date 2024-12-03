/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.util.BitSet;

public final class BitSetUtils {
    private BitSetUtils() {
    }

    public static boolean hasAtLeastOneBitSet(BitSet bitSet, Iterable<Integer> indexes) {
        for (Integer index : indexes) {
            if (!bitSet.get(index)) continue;
            return true;
        }
        return false;
    }

    public static boolean hasAllBitsSet(BitSet bitSet, Iterable<Integer> indexes) {
        for (Integer index : indexes) {
            if (bitSet.get(index)) continue;
            return false;
        }
        return true;
    }

    public static void setBits(BitSet bitSet, Iterable<Integer> indexes) {
        for (Integer index : indexes) {
            bitSet.set(index);
        }
    }

    public static void unsetBits(BitSet bitSet, Iterable<Integer> indexes) {
        for (Integer index : indexes) {
            bitSet.set((int)index, false);
        }
    }
}

