/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class AsyncExportFinishedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -37107763729780970L;
    private final String exportType;
    private final String exportScope;
    private final String spaceKey;

    public AsyncExportFinishedEvent(Object src, String exportType, String exportScope, String spaceKey) {
        super(src);
        this.exportType = exportType;
        this.exportScope = exportScope;
        this.spaceKey = spaceKey;
    }

    public String getExportType() {
        return this.exportType;
    }

    public String getExportScope() {
        return this.exportScope;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

