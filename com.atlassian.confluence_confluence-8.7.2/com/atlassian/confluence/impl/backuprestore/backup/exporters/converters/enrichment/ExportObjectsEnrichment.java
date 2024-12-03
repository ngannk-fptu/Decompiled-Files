/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment;

import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import java.util.List;

public interface ExportObjectsEnrichment {
    public void enrichElements(List<EntityObjectReadyForExport> var1);
}

