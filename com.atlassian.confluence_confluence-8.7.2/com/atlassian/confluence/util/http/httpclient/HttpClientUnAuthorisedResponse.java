/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http.httpclient;

import com.atlassian.confluence.util.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Deprecated(forRemoval=true)
public class HttpClientUnAuthorisedResponse
implements HttpResponse {
    private static final String BLOCKED_URL_MSG = "Blocked or Unauthorised Url";

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return true;
    }

    @Override
    public boolean isNotFound() {
        return false;
    }

    @Override
    public boolean isNotPermitted() {
        return true;
    }

    @Override
    public InputStream getResponse() throws IOException {
        throw new UnsupportedOperationException(BLOCKED_URL_MSG);
    }

    @Override
    public URI getResponseURI() {
        throw new UnsupportedOperationException(BLOCKED_URL_MSG);
    }

    @Override
    public String getCharset() {
        return "utf-8";
    }

    @Override
    public String getMIMEType() {
        return this.getContentType();
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String[] getHeaders(String name) {
        throw new UnsupportedOperationException(BLOCKED_URL_MSG);
    }

    @Override
    public String getStatusMessage() {
        return "Forbidden: Blocked or Unauthorised Url";
    }

    @Override
    public int getStatusCode() {
        return 403;
    }

    @Override
    public void finish() {
        throw new UnsupportedOperationException(BLOCKED_URL_MSG);
    }
}

