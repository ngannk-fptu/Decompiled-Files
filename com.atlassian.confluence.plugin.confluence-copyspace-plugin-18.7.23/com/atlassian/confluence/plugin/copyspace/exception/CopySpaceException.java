/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 */
package com.atlassian.confluence.plugin.copyspace.exception;

import com.atlassian.confluence.core.ConfluenceEntityObject;

public class CopySpaceException
extends RuntimeException {
    private final ConfluenceEntityObject failedContent;

    public CopySpaceException(String message) {
        this(message, null, null);
    }

    public CopySpaceException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public CopySpaceException(String message, ConfluenceEntityObject failedContent, Throwable cause) {
        super(message, cause);
        this.failedContent = failedContent;
    }

    public ConfluenceEntityObject getFailedContent() {
        return this.failedContent;
    }
}

