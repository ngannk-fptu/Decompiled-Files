/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.confluence.exception;

public class ConfluenceCloudServiceException
extends RuntimeException {
    public ConfluenceCloudServiceException(String message) {
        super(message);
    }

    public ConfluenceCloudServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

