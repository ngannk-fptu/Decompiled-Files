/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;

@Deprecated
public class ExportFinishedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -8906407686830911453L;
    private final String exportType;
    private final String exportScope;
    private final String spaceKey;

    public ExportFinishedEvent(Object src, String exportType, String exportScope, String spaceKey) {
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

