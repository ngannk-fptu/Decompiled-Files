/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.backuprestore.exception;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;

public class NotPermittedException
extends BackupRestoreException {
    public NotPermittedException(String message) {
        super(message);
    }
}

