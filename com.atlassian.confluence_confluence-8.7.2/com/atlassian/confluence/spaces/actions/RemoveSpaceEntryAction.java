/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.RemoveSpaceViewEvent;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@WebSudoRequired
public class RemoveSpaceEntryAction
extends AbstractSpaceAdminAction
implements Evented<RemoveSpaceViewEvent> {
    private static final String QUEUE_WARNING_SIZE_PROP = "remove.space.index.queue.warn.size";
    private static final int QUEUE_WARN_DEFAULT_SIZE = 5000;
    private IndexManager indexManager;

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public boolean isShowLargeQueueWarning() {
        int queueWarnSize = this.getIndexQueueSize();
        return this.indexManager.getQueueSize() > queueWarnSize;
    }

    public int getIndexQueueSize() {
        return Integer.getInteger(QUEUE_WARNING_SIZE_PROP, 5000);
    }

    @Override
    public RemoveSpaceViewEvent getEventToPublish(String result) {
        return new RemoveSpaceViewEvent(this, this.getSpace());
    }
}

