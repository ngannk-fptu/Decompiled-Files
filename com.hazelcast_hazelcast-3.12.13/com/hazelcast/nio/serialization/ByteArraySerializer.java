/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.Serializer;
import java.io.IOException;

public interface ByteArraySerializer<T>
extends Serializer {
    public byte[] write(T var1) throws IOException;

    public T read(byte[] var1) throws IOException;
}

