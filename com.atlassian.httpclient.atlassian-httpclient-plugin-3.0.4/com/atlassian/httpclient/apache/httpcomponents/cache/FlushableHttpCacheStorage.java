/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents.cache;

import java.util.regex.Pattern;
import org.apache.http.client.cache.HttpCacheStorage;

public interface FlushableHttpCacheStorage
extends HttpCacheStorage {
    public void flushByUriPattern(Pattern var1);
}

