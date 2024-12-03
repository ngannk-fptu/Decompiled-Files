/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import java.util.List;

public interface PostExportAction {
    public void apply(List<EntityObjectReadyForExport> var1) throws BackupRestoreException;
}

