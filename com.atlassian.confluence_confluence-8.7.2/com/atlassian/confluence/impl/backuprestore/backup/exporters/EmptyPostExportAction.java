/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.PostExportAction;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import java.util.List;

public class EmptyPostExportAction
implements PostExportAction {
    @Override
    public void apply(List<EntityObjectReadyForExport> entities) {
    }
}

