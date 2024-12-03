/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.serialization;

import com.hazelcast.spi.serialization.SerializationService;

public interface SerializationServiceAware {
    public void setSerializationService(SerializationService var1);
}

