/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.wan.WanReplicationService;

public class WanCheckConsistencyOperation
extends AbstractLocalOperation {
    private String schemeName;
    private String publisherName;
    private String mapName;

    public WanCheckConsistencyOperation(String schemeName, String publisherName, String mapName) {
        this.schemeName = schemeName;
        this.publisherName = publisherName;
        this.mapName = mapName;
    }

    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine = this.getNodeEngine();
        WanReplicationService wanReplicationService = nodeEngine.getWanReplicationService();
        wanReplicationService.consistencyCheck(this.schemeName, this.publisherName, this.mapName);
    }
}

