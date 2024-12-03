/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ImportExportException
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.activeobjects.spi.ImportExportException;

public class DbDumpException
extends ImportExportException {
    public DbDumpException(String message) {
        super(message);
    }

    public DbDumpException(String message, Throwable cause) {
        super(message, cause);
    }
}

