/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.collect;

public final class LongBitSet {
    public static final int MAX_SIZE = 64;

    public static int size(long bitset) {
        return Long.bitCount(bitset);
    }

    public static boolean isSet(long bitset, long i) {
        return (bitset & (long)(1 << (int)i)) != 0L;
    }

    public static long unsetBit(long bitset, long i) {
        return bitset & (long)(~(1 << (int)i));
    }

    public static long setBit(long bitset, long i) {
        return bitset | (long)(1 << (int)i);
    }
}

