/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class ClusterIndexSnapshotRequestEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -6810930430609941004L;
    private final String receiverNodeId;
    private final String senderNodeId;
    private final JournalIdentifier journalId;
    private final String indexDirName;

    public ClusterIndexSnapshotRequestEvent(Object src, String senderNodeId, String receiverNodeId, JournalIdentifier journalId, String indexDirName) {
        super(src);
        this.receiverNodeId = receiverNodeId;
        this.senderNodeId = senderNodeId;
        this.journalId = journalId;
        this.indexDirName = indexDirName;
    }

    public String getReceiverNodeId() {
        return this.receiverNodeId;
    }

    public String getSenderNodeId() {
        return this.senderNodeId;
    }

    public JournalIdentifier getJournalId() {
        return this.journalId;
    }

    public String getIndexDirName() {
        return this.indexDirName;
    }
}

