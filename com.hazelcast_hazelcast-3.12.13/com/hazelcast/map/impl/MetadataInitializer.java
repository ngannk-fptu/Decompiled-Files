/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public interface MetadataInitializer {
    public Object createFromData(Data var1) throws IOException;

    public Object createFromObject(Object var1) throws IOException;
}

