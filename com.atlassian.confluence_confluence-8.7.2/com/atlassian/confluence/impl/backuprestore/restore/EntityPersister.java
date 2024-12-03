/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Collection;

public interface EntityPersister {
    public Collection<Class<?>> getSupportedClasses();

    public boolean canAccept(ImportedObjectV2 var1);

    public void persist(ImportedObjectV2 var1) throws BackupRestoreException;

    public long persistNextChunkOfData() throws BackupRestoreException;
}

