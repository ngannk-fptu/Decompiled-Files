/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.admin.AbstractAsyncImportEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.import.finished")
public class AsyncImportFinishedEvent
extends AbstractAsyncImportEvent
implements ClusterEvent {
    private static final long serialVersionUID = -3520118315529827649L;

    public AsyncImportFinishedEvent(Object src, ImportContext importContext) {
        super(src, importContext);
    }

    @Override
    public boolean isOriginalEvent() {
        return false;
    }
}

