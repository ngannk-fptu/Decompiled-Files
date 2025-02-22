/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaChangeEvent;
import com.hazelcast.logging.ILogger;

final class InternalPartitionListener
implements PartitionListener {
    private final InternalPartitionServiceImpl partitionService;
    private final ILogger logger;
    private volatile PartitionListenerNode listenerHead;

    InternalPartitionListener(Node node, InternalPartitionServiceImpl partitionService) {
        this.partitionService = partitionService;
        this.logger = node.getLogger(InternalPartitionService.class);
    }

    @Override
    public void replicaChanged(PartitionReplicaChangeEvent event) {
        int partitionId = event.getPartitionId();
        int replicaIndex = event.getReplicaIndex();
        if (replicaIndex == 0) {
            this.partitionService.getReplicaManager().cancelReplicaSync(partitionId);
        }
        if (this.partitionService.isLocalMemberMaster()) {
            this.partitionService.getPartitionStateManager().incrementVersion();
        }
        this.callListeners(event);
    }

    private void callListeners(PartitionReplicaChangeEvent event) {
        PartitionListenerNode listenerNode = this.listenerHead;
        while (listenerNode != null) {
            try {
                listenerNode.listener.replicaChanged(event);
            }
            catch (Throwable e) {
                this.logger.warning("While calling PartitionListener: " + listenerNode.listener, e);
            }
            listenerNode = listenerNode.next;
        }
    }

    void addChildListener(PartitionListener listener) {
        PartitionListenerNode head = this.listenerHead;
        this.listenerHead = new PartitionListenerNode(listener, head);
    }

    private static final class PartitionListenerNode {
        final PartitionListener listener;
        final PartitionListenerNode next;

        PartitionListenerNode(PartitionListener listener, PartitionListenerNode next) {
            this.listener = listener;
            this.next = next;
        }
    }
}

