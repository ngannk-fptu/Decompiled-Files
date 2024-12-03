/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import com.atlassian.confluence.util.http.HttpRequest;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.HttpRetrievalServiceConfig;
import java.io.IOException;

@Deprecated(forRemoval=true)
public interface HttpRetrievalService {
    public static final boolean NET_REQUEST_ALLOW_ALL_HOSTS = Boolean.getBoolean("net.request.allow.all.hosts");
    public static final int DEFAULT_MAX_DOWNLOAD_SIZE = 524288000;
    public static final int DEFAULT_MAX_CACHE_AGE = 1800000;
    public static final int HTTP_INCLUDE_STACK_MAX_DEPTH = 1;
    public static final String HTTP_USER_AGENT_STRING = "Confluence/{0} (http://www.atlassian.com/software/confluence)";

    @Deprecated
    public HttpResponse get(String var1) throws IOException;

    @Deprecated
    public HttpResponse get(HttpRequest var1) throws IOException;

    @Deprecated
    public HttpRequest getDefaultRequestFor(String var1);

    @Deprecated
    public Class[] getAvailableAuthenticators();

    @Deprecated
    public HttpRetrievalServiceConfig getHttpRetrievalServiceConfig();

    @Deprecated
    public void setHttpRetrievalServiceConfig(HttpRetrievalServiceConfig var1);
}

