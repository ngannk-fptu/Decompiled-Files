/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.httpclient.api.ResponseTransformation;
import java.net.URI;
import java.util.regex.Pattern;

public interface HttpClient {
    public Request.Builder newRequest();

    public Request.Builder newRequest(URI var1);

    public Request.Builder newRequest(String var1);

    public Request.Builder newRequest(URI var1, String var2, String var3);

    public Request.Builder newRequest(String var1, String var2, String var3);

    public void flushCacheByUriPattern(Pattern var1);

    public <A> ResponseTransformation.Builder<A> transformation();

    public ResponsePromise execute(Request var1);
}

