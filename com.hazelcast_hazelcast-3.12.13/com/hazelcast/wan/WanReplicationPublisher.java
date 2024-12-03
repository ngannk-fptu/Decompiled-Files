/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.WanReplicationEvent;

public interface WanReplicationPublisher {
    public void publishReplicationEvent(String var1, ReplicationEventObject var2);

    public void publishReplicationEventBackup(String var1, ReplicationEventObject var2);

    public void publishReplicationEvent(WanReplicationEvent var1);

    public void checkWanReplicationQueues();
}

