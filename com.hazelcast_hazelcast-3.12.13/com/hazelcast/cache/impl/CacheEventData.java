/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public interface CacheEventData
extends IdentifiedDataSerializable {
    public CacheEventType getCacheEventType();

    public String getName();

    public Data getDataKey();

    public Data getDataValue();

    public Data getDataOldValue();

    public boolean isOldValueAvailable();
}

