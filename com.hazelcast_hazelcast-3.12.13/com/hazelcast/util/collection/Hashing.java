/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.HashUtil;

public final class Hashing {
    private Hashing() {
    }

    static int intHash(int value, int mask) {
        return HashUtil.fastIntMix(value) & mask;
    }

    static int longHash(long value, int mask) {
        return (int)HashUtil.fastLongMix(value) & mask;
    }

    static int evenLongHash(long value, int mask) {
        int h = (int)HashUtil.fastLongMix(value);
        return h & mask & 0xFFFFFFFE;
    }

    static int hash(Object value, int mask) {
        return HashUtil.fastIntMix(value.hashCode()) & mask;
    }

    static int hashCode(long value) {
        return (int)(value ^ value >>> 32);
    }
}

