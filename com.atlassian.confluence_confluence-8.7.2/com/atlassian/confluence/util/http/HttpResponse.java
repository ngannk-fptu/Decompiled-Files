/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Deprecated(forRemoval=true)
public interface HttpResponse {
    public boolean isCached();

    public boolean isFailed();

    public boolean isNotFound();

    public boolean isNotPermitted();

    public InputStream getResponse() throws IOException;

    public URI getResponseURI();

    public String getCharset();

    public String getMIMEType();

    public String getContentType();

    public String[] getHeaders(String var1);

    public String getStatusMessage();

    public int getStatusCode();

    public void finish();
}

