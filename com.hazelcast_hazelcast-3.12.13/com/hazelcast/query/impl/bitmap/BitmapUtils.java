/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.bitmap;

final class BitmapUtils {
    private static final int SHORT_TO_INT_MASK = 65535;
    private static final long SHORT_TO_LONG_MASK = 65535L;
    private static final long INT_TO_LONG_MASK = 0xFFFFFFFFL;
    private static final int INT_ARRAY_MIN_CAPACITY = 2;
    private static final int SHORT_ARRAY_MIN_CAPACITY = 4;
    private static final int CAPACITY_SHIFT = 2;

    private BitmapUtils() {
    }

    public static int toUnsignedInt(short value) {
        return value & 0xFFFF;
    }

    public static long toUnsignedLong(short value) {
        return (long)value & 0xFFFFL;
    }

    public static long toUnsignedLong(int value) {
        return (long)value & 0xFFFFFFFFL;
    }

    public static int unsignedBinarySearch(short[] array, int size, int unsignedValue) {
        return BitmapUtils.unsignedBinarySearch(array, 0, size, unsignedValue);
    }

    public static int unsignedBinarySearch(short[] array, int from, int size, int unsignedValue) {
        int right = size - 1;
        int lastValue = BitmapUtils.toUnsignedInt(array[right]);
        if (unsignedValue > lastValue) {
            return -(size + 1);
        }
        if (lastValue == right) {
            return unsignedValue;
        }
        int left = from;
        while (left <= right) {
            int middle = left + right >>> 1;
            int candidate = BitmapUtils.toUnsignedInt(array[middle]);
            if (candidate < unsignedValue) {
                left = middle + 1;
                continue;
            }
            if (candidate > unsignedValue) {
                right = middle - 1;
                continue;
            }
            return middle;
        }
        return -(left + 1);
    }

    public static int unsignedBinarySearch(int[] array, int size, long unsignedValue) {
        return BitmapUtils.unsignedBinarySearch(array, 0, size, unsignedValue);
    }

    public static int unsignedBinarySearch(int[] array, int from, int size, long unsignedValue) {
        int right = size - 1;
        long lastValue = BitmapUtils.toUnsignedLong(array[right]);
        if (unsignedValue > lastValue) {
            return -(size + 1);
        }
        if (lastValue == (long)right) {
            return (int)unsignedValue;
        }
        int left = from;
        while (left <= right) {
            int middle = left + right >>> 1;
            long candidate = BitmapUtils.toUnsignedLong(array[middle]);
            if (candidate < unsignedValue) {
                left = middle + 1;
                continue;
            }
            if (candidate > unsignedValue) {
                right = middle - 1;
                continue;
            }
            return middle;
        }
        return -(left + 1);
    }

    public static int capacityDeltaInt(int capacity) {
        return Math.max(2, capacity >>> 2);
    }

    public static int capacityDeltaShort(int capacity) {
        return Math.max(4, capacity >>> 2);
    }

    public static int denseCapacityDeltaInt(int size, int capacity) {
        assert (capacity > 0);
        assert (size <= capacity);
        if (size == 0) {
            return 0;
        }
        int delta = BitmapUtils.capacityDeltaInt(capacity);
        int wasted = capacity - size;
        return Math.max(0, delta - wasted);
    }

    public static int denseCapacityDeltaShort(int size, int capacity) {
        assert (capacity > 0);
        assert (size <= capacity);
        if (size == 0) {
            return 0;
        }
        int delta = BitmapUtils.capacityDeltaShort(capacity);
        int wasted = capacity - size;
        return Math.max(0, delta - wasted);
    }

    public static int compareUnsigned(int x, int y) {
        return BitmapUtils.compare(x + Integer.MIN_VALUE, y + Integer.MIN_VALUE);
    }

    private static int compare(int x, int y) {
        return x < y ? -1 : (x == y ? 0 : 1);
    }
}

