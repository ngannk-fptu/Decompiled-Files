/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;

@FunctionalInterface
public interface BackupRestoreConsumer<T> {
    public void accept(T var1) throws BackupRestoreException;
}

