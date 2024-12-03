/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.httpclient.apache.httpcomponents.cache;

import com.atlassian.httpclient.apache.httpcomponents.cache.FlushableHttpCacheStorage;
import com.atlassian.httpclient.apache.httpcomponents.cache.ForwardingFlushableHttpCacheStorage;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingHttpCacheStorage
extends ForwardingFlushableHttpCacheStorage {
    private final Logger logger;
    private final FlushableHttpCacheStorage httpCacheStorage;
    private final Supplier<String> instanceId;

    public LoggingHttpCacheStorage(FlushableHttpCacheStorage httpCacheStorage) {
        this.httpCacheStorage = (FlushableHttpCacheStorage)Preconditions.checkNotNull((Object)httpCacheStorage);
        this.instanceId = Suppliers.memoize((Supplier)new Supplier<String>(){

            public String get() {
                return Integer.toHexString(System.identityHashCode(LoggingHttpCacheStorage.this));
            }
        });
        this.logger = LoggerFactory.getLogger(this.delegate().getClass());
    }

    @Override
    protected FlushableHttpCacheStorage delegate() {
        return this.httpCacheStorage;
    }

    @Override
    public void flushByUriPattern(Pattern urlPattern) {
        this.logger.debug("Cache [{}] is flushing entries matching {}", this.instanceId.get(), (Object)urlPattern);
        super.flushByUriPattern(urlPattern);
    }

    @Override
    public void putEntry(String key, HttpCacheEntry entry) throws IOException {
        this.logger.debug("Cache [{}] is adding '{}'s response: {}", new Object[]{this.instanceId.get(), key, LoggingHttpCacheStorage.toString(entry)});
        super.putEntry(key, entry);
    }

    @Override
    public HttpCacheEntry getEntry(String key) throws IOException {
        HttpCacheEntry entry = super.getEntry(key);
        this.logger.debug("Cache [{}] is getting '{}'s response: {}", new Object[]{this.instanceId.get(), key, LoggingHttpCacheStorage.toString(entry)});
        return entry;
    }

    @Override
    public void removeEntry(String key) throws IOException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Cache [{}] is removing '{}''s response: {}", new Object[]{this.instanceId.get(), key, LoggingHttpCacheStorage.toString(super.getEntry(key))});
        }
        super.removeEntry(key);
    }

    @Override
    public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
        if (this.logger.isDebugEnabled()) {
            HttpCacheEntry oldEntry = super.getEntry(key);
            super.updateEntry(key, callback);
            HttpCacheEntry newEntry = super.getEntry(key);
            this.logger.debug("Cache [{}] is updating '{}'s response from {} to {}", new Object[]{this.instanceId.get(), key, LoggingHttpCacheStorage.toString(oldEntry), LoggingHttpCacheStorage.toString(newEntry)});
        } else {
            super.updateEntry(key, callback);
        }
    }

    private static HttpCacheEntryToString toString(HttpCacheEntry httpCacheEntry) {
        return httpCacheEntry == null ? null : new HttpCacheEntryToString(httpCacheEntry);
    }

    private static final class HttpCacheEntryToString {
        private final HttpCacheEntry httpCacheEntry;

        private HttpCacheEntryToString(HttpCacheEntry httpCacheEntry) {
            this.httpCacheEntry = (HttpCacheEntry)Preconditions.checkNotNull((Object)httpCacheEntry);
        }

        public String toString() {
            return this.httpCacheEntry.toString();
        }
    }
}

