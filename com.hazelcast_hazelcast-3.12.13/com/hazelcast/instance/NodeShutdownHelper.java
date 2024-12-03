/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class NodeShutdownHelper {
    private NodeShutdownHelper() {
    }

    public static void shutdownNodeByFiringEvents(Node node, boolean terminate) {
        HazelcastInstanceImpl hazelcastInstance = node.hazelcastInstance;
        LifecycleServiceImpl lifecycleService = hazelcastInstance.getLifecycleService();
        lifecycleService.fireLifecycleEvent(LifecycleEvent.LifecycleState.SHUTTING_DOWN);
        node.shutdown(terminate);
        lifecycleService.fireLifecycleEvent(LifecycleEvent.LifecycleState.SHUTDOWN);
    }
}

