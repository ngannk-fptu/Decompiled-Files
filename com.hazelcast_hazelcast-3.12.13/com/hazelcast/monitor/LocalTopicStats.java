/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalTopicStats
extends LocalInstanceStats {
    @Override
    public long getCreationTime();

    public long getPublishOperationCount();

    public long getReceiveOperationCount();
}

