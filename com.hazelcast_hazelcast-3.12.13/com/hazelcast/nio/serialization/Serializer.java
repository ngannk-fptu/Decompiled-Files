/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

public interface Serializer {
    public int getTypeId();

    public void destroy();
}

