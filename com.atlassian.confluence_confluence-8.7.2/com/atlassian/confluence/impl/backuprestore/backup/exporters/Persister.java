/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import java.util.Collection;

public interface Persister {
    public void persistObjects(Collection<Object> var1) throws InterruptedException, BackupRestoreException;
}

