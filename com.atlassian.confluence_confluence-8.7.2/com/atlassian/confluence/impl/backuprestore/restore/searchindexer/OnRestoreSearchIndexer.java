/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.searchindexer;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Collection;

public interface OnRestoreSearchIndexer {
    public void onObjectsPersisting(Collection<ImportedObjectV2> var1) throws BackupRestoreException;

    public void flush() throws BackupRestoreException;
}

