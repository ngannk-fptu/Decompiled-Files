/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.resourcepool.ResourcePoolEvent;
import java.util.EventListener;

public interface ResourcePoolListener
extends EventListener {
    public void resourceAcquired(ResourcePoolEvent var1);

    public void resourceCheckedIn(ResourcePoolEvent var1);

    public void resourceCheckedOut(ResourcePoolEvent var1);

    public void resourceRemoved(ResourcePoolEvent var1);
}

