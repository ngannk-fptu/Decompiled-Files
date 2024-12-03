/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.serialization.DataSerializable;

public interface ServiceNamespace
extends DataSerializable {
    public String getServiceName();
}

