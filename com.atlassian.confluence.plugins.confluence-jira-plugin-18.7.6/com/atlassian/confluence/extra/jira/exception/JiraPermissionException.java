/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.exception;

import java.net.ProtocolException;

public class JiraPermissionException
extends ProtocolException {
    public JiraPermissionException() {
    }

    public JiraPermissionException(String message) {
        super(message);
    }
}

