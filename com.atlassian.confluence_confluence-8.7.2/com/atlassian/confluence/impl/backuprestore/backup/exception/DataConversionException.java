/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exception;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;

public class DataConversionException
extends BackupRestoreException {
    private static final long serialVersionUID = 1L;

    public DataConversionException(String s) {
        super(s);
    }

    public DataConversionException(String s, Throwable cause) {
        super(s, cause);
    }

    public DataConversionException(Throwable cause) {
        super(cause);
    }
}

