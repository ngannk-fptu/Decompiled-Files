/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.MulticastListener;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.nio.Address;
import java.util.concurrent.BlockingDeque;

public class SplitBrainMulticastListener
implements MulticastListener {
    private final Node node;
    private final BlockingDeque<SplitBrainJoinMessage> deque;

    public SplitBrainMulticastListener(Node node, BlockingDeque<SplitBrainJoinMessage> deque) {
        this.node = node;
        this.deque = deque;
    }

    @Override
    public void onMessage(Object msg) {
        if (msg instanceof SplitBrainJoinMessage) {
            SplitBrainJoinMessage joinRequest = (SplitBrainJoinMessage)msg;
            Address thisAddress = this.node.getThisAddress();
            if (!thisAddress.equals(joinRequest.getAddress()) && this.node.isMaster()) {
                this.deque.addFirst(joinRequest);
            }
        }
    }
}

