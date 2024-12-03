/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public interface DataSerializable {
    public void writeData(ObjectDataOutput var1) throws IOException;

    public void readData(ObjectDataInput var1) throws IOException;
}

