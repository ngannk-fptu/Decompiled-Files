/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.rest;

public class QueryFailedException
extends RuntimeException {
    public QueryFailedException(String message) {
        super(message);
    }

    public QueryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

