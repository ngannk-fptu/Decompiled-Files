/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Serializer;
import java.io.IOException;

public interface StreamSerializer<T>
extends Serializer {
    public void write(ObjectDataOutput var1, T var2) throws IOException;

    public T read(ObjectDataInput var1) throws IOException;
}

