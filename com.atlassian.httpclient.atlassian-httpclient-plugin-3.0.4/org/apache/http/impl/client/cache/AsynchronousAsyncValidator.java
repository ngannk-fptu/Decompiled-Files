/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.impl.client.cache.AsynchronousAsyncValidationRequest;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CacheKeyGenerator;
import org.apache.http.impl.client.cache.CachingHttpAsyncClient;

class AsynchronousAsyncValidator {
    private final CachingHttpAsyncClient cachingAsyncClient;
    private final ExecutorService executor;
    private final Set<String> queued;
    private final CacheKeyGenerator cacheKeyGenerator;
    private final Log log = LogFactory.getLog(this.getClass());

    public AsynchronousAsyncValidator(CachingHttpAsyncClient cachingClient, CacheConfig config) {
        this(cachingClient, new ThreadPoolExecutor(config.getAsynchronousWorkersCore(), config.getAsynchronousWorkersMax(), config.getAsynchronousWorkerIdleLifetimeSecs(), TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(config.getRevalidationQueueSize())));
    }

    AsynchronousAsyncValidator(CachingHttpAsyncClient cachingClient, ExecutorService executor) {
        this.cachingAsyncClient = cachingClient;
        this.executor = executor;
        this.queued = new HashSet<String>();
        this.cacheKeyGenerator = new CacheKeyGenerator();
    }

    public synchronized void revalidateCacheEntry(HttpHost target, HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry entry) {
        String uri = this.cacheKeyGenerator.getVariantURI(target, request, entry);
        if (!this.queued.contains(uri)) {
            AsynchronousAsyncValidationRequest asyncRevalidationRequest = new AsynchronousAsyncValidationRequest(this, this.cachingAsyncClient, target, request, clientContext, entry, uri);
            try {
                this.executor.execute(asyncRevalidationRequest);
                this.queued.add(uri);
            }
            catch (RejectedExecutionException ree) {
                this.log.debug("Revalidation for [" + uri + "] not scheduled: " + ree);
            }
        }
    }

    synchronized void markComplete(String identifier) {
        this.queued.remove(identifier);
    }

    Set<String> getScheduledIdentifiers() {
        return Collections.unmodifiableSet(this.queued);
    }

    ExecutorService getExecutor() {
        return this.executor;
    }
}

