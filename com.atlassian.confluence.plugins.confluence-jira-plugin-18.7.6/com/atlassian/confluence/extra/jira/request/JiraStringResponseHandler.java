/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.http.trust.TrustedConnectionStatus
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.extra.jira.request;

import com.atlassian.confluence.extra.jira.api.services.JiraResponseHandler;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public class JiraStringResponseHandler
implements JiraResponseHandler,
Serializable {
    private String responseBody;

    public String getResponseBody() {
        return this.responseBody;
    }

    @Override
    public void handleJiraResponse(InputStream in, TrustedConnectionStatus trustedConnectionStatus) throws IOException {
        try {
            this.responseBody = IOUtils.toString((InputStream)in, (Charset)Charset.defaultCharset());
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
    }
}

