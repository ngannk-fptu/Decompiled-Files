/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.Connection;

public abstract class AbstractMapPutWithMaxIdleMessageTask<P>
extends AbstractMapPutMessageTask<P> {
    protected AbstractMapPutWithMaxIdleMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void beforeProcess() {
        super.beforeProcess();
        this.checkCompatibility();
    }

    void checkCompatibility() {
        if (this.nodeEngine.getClusterService().getClusterVersion().isLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("Setting MaxIdle is available when cluster version is 3.11 or higher");
        }
    }
}

