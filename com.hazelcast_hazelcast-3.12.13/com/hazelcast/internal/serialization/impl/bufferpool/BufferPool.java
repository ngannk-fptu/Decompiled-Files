/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl.bufferpool;

import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.serialization.Data;

public interface BufferPool {
    public BufferObjectDataOutput takeOutputBuffer();

    public void returnOutputBuffer(BufferObjectDataOutput var1);

    public BufferObjectDataInput takeInputBuffer(Data var1);

    public void returnInputBuffer(BufferObjectDataInput var1);
}

