/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.networking.nio.NioThread;

public interface MigratablePipeline {
    public void requestMigration(NioThread var1);

    public NioThread owner();

    public long load();
}

