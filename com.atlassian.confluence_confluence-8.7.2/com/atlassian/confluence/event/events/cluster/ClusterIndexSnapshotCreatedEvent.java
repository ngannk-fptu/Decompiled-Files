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
public class ClusterIndexSnapshotCreatedEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -1944326701778640068L;
    private final String senderNodeId;
    private final String receiverNodeId;
    private final long journalEntryId;
    private final String indexDirName;
    private final String indexSnapshotFilename;

    @Deprecated
    public ClusterIndexSnapshotCreatedEvent(Object src, String senderNodeId, String receiverNodeId, String indexDirName, String indexSnapshotFilename) {
        super(src);
        this.senderNodeId = senderNodeId;
        this.receiverNodeId = receiverNodeId;
        this.journalEntryId = 0L;
        this.indexDirName = indexDirName;
        this.indexSnapshotFilename = indexSnapshotFilename;
    }

    public ClusterIndexSnapshotCreatedEvent(Object src, String senderNodeId, String receiverNodeId, long journalEntryId, String indexDirName, String indexSnapshotFilename) {
        super(src);
        this.senderNodeId = senderNodeId;
        this.receiverNodeId = receiverNodeId;
        this.journalEntryId = journalEntryId;
        this.indexDirName = indexDirName;
        this.indexSnapshotFilename = indexSnapshotFilename;
    }

    public String getSenderNodeId() {
        return this.senderNodeId;
    }

    public String getReceiverNodeId() {
        return this.receiverNodeId;
    }

    public long getJournalEntryId() {
        return this.journalEntryId;
    }

    public String getIndexDirName() {
        return this.indexDirName;
    }

    public String getIndexSnapshotFilename() {
        return this.indexSnapshotFilename;
    }
}

