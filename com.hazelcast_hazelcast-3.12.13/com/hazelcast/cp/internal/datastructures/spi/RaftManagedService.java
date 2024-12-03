/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi;

import com.hazelcast.spi.ManagedService;

public interface RaftManagedService
extends ManagedService {
    public void onCPSubsystemRestart();
}

