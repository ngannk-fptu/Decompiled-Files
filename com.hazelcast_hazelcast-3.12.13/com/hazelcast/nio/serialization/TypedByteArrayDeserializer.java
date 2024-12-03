/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import java.io.IOException;

public interface TypedByteArrayDeserializer<T> {
    public T read(byte[] var1, Class var2) throws IOException;
}

