/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingObject
 */
package com.atlassian.httpclient.apache.httpcomponents.cache;

import com.google.common.collect.ForwardingObject;
import java.io.IOException;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

public abstract class ForwardingHttpCacheStorage
extends ForwardingObject
implements HttpCacheStorage {
    protected abstract HttpCacheStorage delegate();

    @Override
    public void putEntry(String key, HttpCacheEntry entry) throws IOException {
        this.delegate().putEntry(key, entry);
    }

    @Override
    public HttpCacheEntry getEntry(String key) throws IOException {
        return this.delegate().getEntry(key);
    }

    @Override
    public void removeEntry(String key) throws IOException {
        this.delegate().removeEntry(key);
    }

    @Override
    public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
        this.delegate().updateEntry(key, callback);
    }
}

