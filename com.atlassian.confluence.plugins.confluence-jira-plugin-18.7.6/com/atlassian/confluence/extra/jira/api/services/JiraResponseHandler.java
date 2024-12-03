/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.http.trust.TrustedConnectionStatus
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;
import java.io.IOException;
import java.io.InputStream;

public interface JiraResponseHandler {
    public void handleJiraResponse(InputStream var1, TrustedConnectionStatus var2) throws IOException;

    public static enum HandlerType {
        STRING_HANDLER,
        CHANNEL_HANDLER;

    }
}

