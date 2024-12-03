/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.resourcepool.ResourcePool;
import com.mchange.v2.resourcepool.ResourcePoolException;
import com.mchange.v2.resourcepool.ResourcePoolListener;

public interface EventSupportingResourcePool
extends ResourcePool {
    public void addResourcePoolListener(ResourcePoolListener var1) throws ResourcePoolException;

    public void removeResourcePoolListener(ResourcePoolListener var1) throws ResourcePoolException;
}

