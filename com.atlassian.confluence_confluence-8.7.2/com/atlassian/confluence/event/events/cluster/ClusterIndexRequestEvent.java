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
public class ClusterIndexRequestEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -7104727856522544121L;
    private final String nodeId;
    private final JournalIdentifier journalId;
    private final String buildNumber;
    private final String indexDirName;

    public ClusterIndexRequestEvent(Object src, String senderNodeId, JournalIdentifier journalId, String buildNumber, String indexDirName) {
        super(src);
        this.nodeId = senderNodeId;
        this.journalId = journalId;
        this.buildNumber = buildNumber;
        this.indexDirName = indexDirName;
    }

    public JournalIdentifier getJournalId() {
        return this.journalId;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public String getBuildNumber() {
        return this.buildNumber;
    }

    public String getIndexDirName() {
        return this.indexDirName;
    }
}

