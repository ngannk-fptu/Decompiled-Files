/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav.servlet.filter.exceptions;

public class FailedOperationException
extends RuntimeException {
    private final int status;

    public FailedOperationException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public String getResponseString() {
        return this.getMessage();
    }
}

