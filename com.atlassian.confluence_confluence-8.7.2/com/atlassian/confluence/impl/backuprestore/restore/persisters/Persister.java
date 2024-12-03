/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Collection;
import java.util.Map;

public interface Persister {
    public boolean shouldPersist(Map<ExportableEntityInfo, Collection<ImportedObjectV2>> var1);

    public void persist(Map<ExportableEntityInfo, Collection<ImportedObjectV2>> var1) throws BackupRestoreException;
}

