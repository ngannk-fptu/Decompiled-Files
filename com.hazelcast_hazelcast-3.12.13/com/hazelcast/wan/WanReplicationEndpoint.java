/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.wan.WanReplicationPublisher;

public interface WanReplicationEndpoint
extends WanReplicationPublisher {
    public void init(Node var1, WanReplicationConfig var2, WanPublisherConfig var3);

    public void shutdown();
}

