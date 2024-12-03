/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

public class DuplicateDataRuntimeException
extends IllegalArgumentException {
    private final String dataIdentifier;

    public DuplicateDataRuntimeException(String dataIdentifier, String message, Throwable cause) {
        super(message, cause);
        this.dataIdentifier = dataIdentifier;
    }

    public DuplicateDataRuntimeException(String dataIdentifier, String message) {
        super(message);
        this.dataIdentifier = dataIdentifier;
    }

    public String getDataIdentifier() {
        return this.dataIdentifier;
    }
}

