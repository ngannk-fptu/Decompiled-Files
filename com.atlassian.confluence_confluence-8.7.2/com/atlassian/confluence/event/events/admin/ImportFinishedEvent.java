/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.importexport.ImportContext;

public class ImportFinishedEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -549520563115999078L;
    transient ImportContext importContext;
    private boolean siteImport;

    public ImportFinishedEvent(Object src, ImportContext importContext) {
        super(src);
        this.importContext = importContext;
        this.siteImport = importContext.getSpaceKeyOfSpaceImport() == null;
    }

    public ImportContext getImportContext() {
        return this.importContext;
    }

    public boolean isSiteImport() {
        return this.siteImport;
    }

    public boolean isOriginalEvent() {
        return this.importContext != null;
    }
}

