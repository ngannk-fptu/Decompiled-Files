/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.backuprestore.exception;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;

public class TheSameSpaceBackupRestoreJobAlreadyInProgressException
extends BackupRestoreException {
    public TheSameSpaceBackupRestoreJobAlreadyInProgressException(String message) {
        super(message);
    }
}

