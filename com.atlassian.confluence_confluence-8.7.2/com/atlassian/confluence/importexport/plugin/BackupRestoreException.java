/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.plugin;

public class BackupRestoreException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BackupRestoreException(String msg) {
        super(msg);
    }

    public BackupRestoreException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

