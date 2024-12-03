/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.integration.jira;

public abstract class JiraException
extends RuntimeException {
    public JiraException(String message) {
        super(message);
    }

    public JiraException(String message, Throwable cause) {
        super(message, cause);
    }
}

