/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class ClusterIndexSnapshotRequestReceivedEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -5408008769378459644L;
    private final String receiverNodeId;
    private final String senderNodeId;

    public ClusterIndexSnapshotRequestReceivedEvent(Object src, String senderNodeId, String receiverNodeId) {
        super(src);
        this.receiverNodeId = receiverNodeId;
        this.senderNodeId = senderNodeId;
    }

    public String getSenderNodeId() {
        return this.senderNodeId;
    }

    public String getReceiverNodeId() {
        return this.receiverNodeId;
    }
}

