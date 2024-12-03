/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.List;

public interface StashObjectsReader {
    public List<ImportedObjectV2> readObjects(int var1) throws BackupRestoreException;

    public long getNumberOfRetrievedObjects();

    public boolean hasMoreRecords();
}

