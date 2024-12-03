/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.List;

public interface ImportedObjectsStash {
    public void add(ImportedObjectV2 var1) throws BackupRestoreException;

    public List<ImportedObjectV2> readObjects(int var1) throws BackupRestoreException;

    public String getName();

    public Integer getIterationNumber();

    public long getNumberOfWrittenObjects();

    public long getNumberOfRetrievedObjects();

    public boolean hasMoreRecords();
}

