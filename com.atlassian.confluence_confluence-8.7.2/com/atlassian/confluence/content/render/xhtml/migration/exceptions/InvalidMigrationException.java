/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

public class InvalidMigrationException
extends RuntimeException {
    public InvalidMigrationException() {
    }

    public InvalidMigrationException(String message) {
        super(message);
    }

    public InvalidMigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMigrationException(Throwable cause) {
        super(cause);
    }
}

