/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

public interface ByteAccessStrategy<R> {
    public byte getByte(R var1, long var2);

    public void putByte(R var1, long var2, byte var4);
}

