/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.plugin.rest.exception;

import com.atlassian.crowd.exception.CrowdException;

public class DirectoryTestFailedException
extends CrowdException {
    public DirectoryTestFailedException() {
    }

    public DirectoryTestFailedException(String message) {
        super(message);
    }

    public DirectoryTestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryTestFailedException(Throwable cause) {
        super(cause);
    }
}

