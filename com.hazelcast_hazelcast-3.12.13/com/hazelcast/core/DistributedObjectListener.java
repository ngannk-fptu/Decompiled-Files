/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObjectEvent;
import java.util.EventListener;

public interface DistributedObjectListener
extends EventListener {
    public void distributedObjectCreated(DistributedObjectEvent var1);

    public void distributedObjectDestroyed(DistributedObjectEvent var1);
}

