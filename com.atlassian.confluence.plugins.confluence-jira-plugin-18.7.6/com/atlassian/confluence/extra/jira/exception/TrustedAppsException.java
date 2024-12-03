/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.confluence.extra.jira.exception;

import com.atlassian.sal.api.net.ResponseException;

public class TrustedAppsException
extends ResponseException {
    public TrustedAppsException(String message) {
        super(message);
    }
}

