/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import java.io.IOException;

public interface Portable {
    public int getFactoryId();

    public int getClassId();

    public void writePortable(PortableWriter var1) throws IOException;

    public void readPortable(PortableReader var1) throws IOException;
}

