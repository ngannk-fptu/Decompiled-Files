/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.wan.WanReplicationService;

public class ClearWanQueuesOperation
extends AbstractLocalOperation {
    private String schemeName;
    private String publisherName;

    public ClearWanQueuesOperation(String schemeName, String publisherName) {
        this.schemeName = schemeName;
        this.publisherName = publisherName;
    }

    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine = this.getNodeEngine();
        WanReplicationService wanReplicationService = nodeEngine.getWanReplicationService();
        wanReplicationService.clearQueues(this.schemeName, this.publisherName);
    }
}

