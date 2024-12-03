/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents.cache;

import com.atlassian.httpclient.apache.httpcomponents.cache.FlushableHttpCacheStorage;
import com.atlassian.httpclient.apache.httpcomponents.cache.ForwardingHttpCacheStorage;
import java.util.regex.Pattern;

public abstract class ForwardingFlushableHttpCacheStorage
extends ForwardingHttpCacheStorage
implements FlushableHttpCacheStorage {
    @Override
    protected abstract FlushableHttpCacheStorage delegate();

    @Override
    public void flushByUriPattern(Pattern urlPattern) {
        this.delegate().flushByUriPattern(urlPattern);
    }
}

