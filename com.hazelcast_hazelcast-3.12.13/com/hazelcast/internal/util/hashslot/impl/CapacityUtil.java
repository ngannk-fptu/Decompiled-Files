/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.util.QuickMath;

public final class CapacityUtil {
    public static final int MAX_INT_CAPACITY = 0x40000000;
    public static final long MAX_LONG_CAPACITY = 0x4000000000000000L;
    public static final int MIN_CAPACITY = 4;
    public static final int DEFAULT_CAPACITY = 16;
    public static final float DEFAULT_LOAD_FACTOR = 0.6f;

    private CapacityUtil() {
    }

    public static long roundCapacity(long requestedCapacity) {
        if (requestedCapacity > 0x4000000000000000L) {
            throw new IllegalArgumentException(requestedCapacity + " is greater than max allowed capacity[" + 0x4000000000000000L + "].");
        }
        return Math.max(4L, QuickMath.nextPowerOfTwo(requestedCapacity));
    }

    public static int roundCapacity(int requestedCapacity) {
        if (requestedCapacity > 0x40000000) {
            throw new IllegalArgumentException(requestedCapacity + " is greater than max allowed capacity[" + 0x40000000 + "].");
        }
        return Math.max(4, QuickMath.nextPowerOfTwo(requestedCapacity));
    }

    public static int nextCapacity(int current) {
        assert (current > 0 && Long.bitCount(current) == 1) : "Capacity must be a power of two.";
        if (current < 2) {
            current = 2;
        }
        if ((current <<= 1) < 0) {
            throw new RuntimeException("Maximum capacity exceeded.");
        }
        return current;
    }

    public static long nextCapacity(long current) {
        assert (current > 0L && Long.bitCount(current) == 1) : "Capacity must be a power of two, but was " + current;
        if (current < 2L) {
            current = 2L;
        }
        if ((current <<= 1) < 0L) {
            throw new RuntimeException("Maximum capacity exceeded.");
        }
        return current;
    }
}

