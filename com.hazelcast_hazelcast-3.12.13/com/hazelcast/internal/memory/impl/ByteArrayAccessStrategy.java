/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.ByteAccessStrategy;

public final class ByteArrayAccessStrategy
implements ByteAccessStrategy<byte[]> {
    public static final ByteArrayAccessStrategy INSTANCE = new ByteArrayAccessStrategy();

    private ByteArrayAccessStrategy() {
    }

    @Override
    public byte getByte(byte[] array, long offset) {
        ByteArrayAccessStrategy.assertFitsInt(offset);
        return array[(int)offset];
    }

    @Override
    public void putByte(byte[] array, long offset, byte x) {
        ByteArrayAccessStrategy.assertFitsInt(offset);
        array[(int)offset] = x;
    }

    private static void assertFitsInt(long arg) {
        assert (arg >= 0L && arg <= Integer.MAX_VALUE) : "argument outside of int range: " + arg;
    }
}

