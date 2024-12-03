/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

public abstract class ImportExportException
extends RuntimeException {
    public ImportExportException(String message) {
        super(message);
    }

    public ImportExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportExportException(Throwable cause) {
        super(cause);
    }
}

