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
public class ClusterIndexResponseEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -2032835108993543499L;
    private final String senderNodeId;
    private final String receiverNodeId;
    private final JournalIdentifier journalId;
    private final String indexDirName;

    public ClusterIndexResponseEvent(Object src, String senderNodeId, String receiverNodeId, JournalIdentifier journalId, String indexDirName) {
        super(src);
        this.senderNodeId = senderNodeId;
        this.receiverNodeId = receiverNodeId;
        this.journalId = journalId;
        this.indexDirName = indexDirName;
    }

    public String getSenderNodeId() {
        return this.senderNodeId;
    }

    public String getReceiverNodeId() {
        return this.receiverNodeId;
    }

    public JournalIdentifier getJournalId() {
        return this.journalId;
    }

    public String getIndexDirName() {
        return this.indexDirName;
    }
}

