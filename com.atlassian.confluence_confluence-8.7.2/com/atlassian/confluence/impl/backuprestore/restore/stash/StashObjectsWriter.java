/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;

public interface StashObjectsWriter {
    public void writeObject(ImportedObjectV2 var1) throws BackupRestoreException;

    public long getNumberOfWrittenObjects();

    public void close() throws BackupRestoreException;
}

