/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.export;

public class MigrationExportException
extends RuntimeException {
    public MigrationExportException(String message) {
        super(message);
    }

    public MigrationExportException(String message, Throwable cause) {
        super(message, cause);
    }
}

