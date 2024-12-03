/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.exception;

public class JiraRuntimeException
extends RuntimeException {
    public JiraRuntimeException() {
    }

    public JiraRuntimeException(String message) {
        super(message);
    }

    public JiraRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

