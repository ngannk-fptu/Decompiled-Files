/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.impl.MutatingOperation;

public class CheckAndEvictOperation
extends QueueOperation
implements MutatingOperation {
    public CheckAndEvictOperation() {
    }

    public CheckAndEvictOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        if (queueContainer.isEvictable()) {
            ProxyService proxyService = this.getNodeEngine().getProxyService();
            proxyService.destroyDistributedObject(this.getServiceName(), this.name);
        }
    }

    @Override
    public int getId() {
        return 35;
    }
}

