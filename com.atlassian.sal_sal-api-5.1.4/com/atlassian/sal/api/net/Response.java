/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.ResponseException;
import java.io.InputStream;
import java.util.Map;

public interface Response {
    public int getStatusCode();

    public String getResponseBodyAsString() throws ResponseException;

    public InputStream getResponseBodyAsStream() throws ResponseException;

    public <T> T getEntity(Class<T> var1) throws ResponseException;

    public String getStatusText();

    public boolean isSuccessful();

    public String getHeader(String var1);

    public Map<String, String> getHeaders();
}

