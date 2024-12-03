/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.httpclient.api;

import io.atlassian.fugue.Option;
import java.io.InputStream;
import java.util.Map;

public interface Message {
    public String getContentType();

    public String getContentCharset();

    public InputStream getEntityStream() throws IllegalStateException;

    public String getEntity() throws IllegalStateException, IllegalArgumentException;

    public boolean hasEntity();

    public boolean hasReadEntity();

    public Map<String, String> getHeaders();

    public String getHeader(String var1);

    public Option<Long> getContentLength();
}

