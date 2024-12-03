/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.impl;

import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.WanReplicationEndpoint;
import com.hazelcast.wan.WanReplicationEvent;
import com.hazelcast.wan.WanReplicationPublisher;

final class WanReplicationPublisherDelegate
implements WanReplicationPublisher {
    final String name;
    final WanReplicationEndpoint[] endpoints;

    public WanReplicationPublisherDelegate(String name, WanReplicationEndpoint[] endpoints) {
        this.name = name;
        this.endpoints = endpoints;
    }

    public WanReplicationEndpoint[] getEndpoints() {
        return this.endpoints;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void publishReplicationEvent(String serviceName, ReplicationEventObject eventObject) {
        for (WanReplicationEndpoint endpoint : this.endpoints) {
            endpoint.publishReplicationEvent(serviceName, eventObject);
        }
    }

    @Override
    public void publishReplicationEventBackup(String serviceName, ReplicationEventObject eventObject) {
    }

    @Override
    public void publishReplicationEvent(WanReplicationEvent wanReplicationEvent) {
    }

    @Override
    public void checkWanReplicationQueues() {
        for (WanReplicationEndpoint endpoint : this.endpoints) {
            endpoint.checkWanReplicationQueues();
        }
    }
}

