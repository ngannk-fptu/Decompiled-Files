/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.backuprestore.exception;

public class BackupRestoreException
extends Exception {
    public BackupRestoreException(Throwable cause) {
        super(cause);
    }

    public BackupRestoreException(String message) {
        super(message);
    }

    public BackupRestoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

