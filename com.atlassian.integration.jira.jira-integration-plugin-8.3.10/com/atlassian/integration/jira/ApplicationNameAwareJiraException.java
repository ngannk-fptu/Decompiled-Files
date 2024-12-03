/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.JiraException;

public abstract class ApplicationNameAwareJiraException
extends JiraException {
    public ApplicationNameAwareJiraException(String message) {
        super(message);
    }

    public ApplicationNameAwareJiraException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getApplicationName();
}

