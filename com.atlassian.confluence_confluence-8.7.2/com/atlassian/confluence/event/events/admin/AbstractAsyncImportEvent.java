/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import java.util.Objects;

abstract class AbstractAsyncImportEvent
extends ConfluenceEvent {
    private final boolean siteImport;
    private final boolean cloudImport;
    private final transient ImportContext importContext;

    public AbstractAsyncImportEvent(Object src, ImportContext importContext) {
        super(src);
        Objects.requireNonNull(importContext);
        this.siteImport = importContext.getSpaceKeyOfSpaceImport() == null;
        this.cloudImport = importContext.getExportDescriptor() != null && importContext.getExportDescriptor().getSource() == ExportDescriptor.Source.CLOUD;
        this.importContext = importContext;
    }

    public ImportContext getImportContext() {
        return this.importContext;
    }

    public boolean isSiteImport() {
        return this.siteImport;
    }

    public boolean isCloudImport() {
        return this.cloudImport;
    }

    @Deprecated
    public boolean isOriginalEvent() {
        return this.importContext != null;
    }
}

