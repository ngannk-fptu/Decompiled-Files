/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.ObjectDataInput;
import java.io.IOException;

public interface TypedStreamDeserializer<T> {
    public T read(ObjectDataInput var1, Class var2) throws IOException;
}

