/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.DistributedObject;

public interface RemoteService {
    public DistributedObject createDistributedObject(String var1);

    public void destroyDistributedObject(String var1);
}

