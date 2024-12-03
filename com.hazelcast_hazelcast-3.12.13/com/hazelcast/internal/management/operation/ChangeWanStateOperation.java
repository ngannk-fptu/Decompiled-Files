/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.config.WanPublisherState;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.wan.WanReplicationService;

public class ChangeWanStateOperation
extends AbstractLocalOperation {
    private String schemeName;
    private String publisherName;
    private WanPublisherState state;

    public ChangeWanStateOperation(String schemeName, String publisherName, WanPublisherState state) {
        this.schemeName = schemeName;
        this.publisherName = publisherName;
        this.state = state;
    }

    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine = this.getNodeEngine();
        WanReplicationService wanReplicationService = nodeEngine.getWanReplicationService();
        switch (this.state) {
            case REPLICATING: {
                wanReplicationService.resume(this.schemeName, this.publisherName);
                break;
            }
            case PAUSED: {
                wanReplicationService.pause(this.schemeName, this.publisherName);
                break;
            }
            case STOPPED: {
                wanReplicationService.stop(this.schemeName, this.publisherName);
                break;
            }
        }
    }
}

