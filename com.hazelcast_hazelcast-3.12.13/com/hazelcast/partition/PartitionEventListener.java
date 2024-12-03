/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition;

import com.hazelcast.partition.PartitionEvent;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.EventListener;

@PrivateApi
public interface PartitionEventListener<T extends PartitionEvent>
extends EventListener {
    public void onEvent(T var1);
}

