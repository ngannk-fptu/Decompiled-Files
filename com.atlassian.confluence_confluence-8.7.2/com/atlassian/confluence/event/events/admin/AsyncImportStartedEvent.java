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
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.import.started")
public class AsyncImportStartedEvent
extends AbstractAsyncImportEvent {
    private static final long serialVersionUID = -3048739510261944458L;

    public AsyncImportStartedEvent(Object src, ImportContext importContext) {
        super(src, importContext);
    }
}

