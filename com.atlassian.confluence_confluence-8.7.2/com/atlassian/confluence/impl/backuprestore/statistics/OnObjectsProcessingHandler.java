/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.statistics;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import java.util.Collection;

public interface OnObjectsProcessingHandler {
    public void onObjectsPersist(Collection<ImportedObjectV2> var1) throws BackupRestoreException;

    public void onObjectsSkipping(Collection<ImportedObjectV2> var1, SkippedObjectsReason var2);

    public void onObjectsReusing(Collection<ImportedObjectV2> var1);
}

