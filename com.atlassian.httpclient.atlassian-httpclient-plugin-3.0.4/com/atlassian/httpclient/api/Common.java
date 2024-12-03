/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import java.io.InputStream;
import java.util.Map;

public interface Common<B extends Common<B>> {
    public B setHeader(String var1, String var2);

    public B setHeaders(Map<String, String> var1);

    public B setEntity(String var1);

    public B setEntityStream(InputStream var1);

    public B setContentCharset(String var1);

    public B setContentType(String var1);

    public B setEntityStream(InputStream var1, String var2);
}

