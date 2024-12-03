/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Serializer;
import java.io.IOException;

interface SerializerAdapter {
    public void write(ObjectDataOutput var1, Object var2) throws IOException;

    public Object read(ObjectDataInput var1) throws IOException;

    public int getTypeId();

    public void destroy();

    public Serializer getImpl();

    public Object read(ObjectDataInput var1, Class var2) throws IOException;
}

