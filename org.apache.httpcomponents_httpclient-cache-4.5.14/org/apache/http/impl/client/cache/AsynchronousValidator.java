/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpRequest
 *  org.apache.http.client.methods.HttpExecutionAware
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.conn.routing.HttpRoute
 */
package org.apache.http.impl.client.cache;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpRequest;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.cache.AsynchronousValidationRequest;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CacheKeyGenerator;
import org.apache.http.impl.client.cache.CachingExec;
import org.apache.http.impl.client.cache.DefaultFailureCache;
import org.apache.http.impl.client.cache.FailureCache;
import org.apache.http.impl.client.cache.ImmediateSchedulingStrategy;
import org.apache.http.impl.client.cache.SchedulingStrategy;

class AsynchronousValidator
implements Closeable {
    private final SchedulingStrategy schedulingStrategy;
    private final Set<String> queued;
    private final CacheKeyGenerator cacheKeyGenerator;
    private final FailureCache failureCache;
    private final Log log = LogFactory.getLog(this.getClass());

    public AsynchronousValidator(CacheConfig config) {
        this(new ImmediateSchedulingStrategy(config));
    }

    AsynchronousValidator(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
        this.queued = new HashSet<String>();
        this.cacheKeyGenerator = new CacheKeyGenerator();
        this.failureCache = new DefaultFailureCache();
    }

    @Override
    public void close() throws IOException {
        this.schedulingStrategy.close();
    }

    public synchronized void revalidateCacheEntry(CachingExec cachingExec, HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, HttpCacheEntry entry) {
        String uri = this.cacheKeyGenerator.getVariantURI(context.getTargetHost(), (HttpRequest)request, entry);
        if (!this.queued.contains(uri)) {
            int consecutiveFailedAttempts = this.failureCache.getErrorCount(uri);
            AsynchronousValidationRequest revalidationRequest = new AsynchronousValidationRequest(this, cachingExec, route, request, context, execAware, entry, uri, consecutiveFailedAttempts);
            try {
                this.schedulingStrategy.schedule(revalidationRequest);
                this.queued.add(uri);
            }
            catch (RejectedExecutionException ree) {
                this.log.debug((Object)("Revalidation for [" + uri + "] not scheduled: " + ree));
            }
        }
    }

    synchronized void markComplete(String identifier) {
        this.queued.remove(identifier);
    }

    void jobSuccessful(String identifier) {
        this.failureCache.resetErrorCount(identifier);
    }

    void jobFailed(String identifier) {
        this.failureCache.increaseErrorCount(identifier);
    }

    Set<String> getScheduledIdentifiers() {
        return Collections.unmodifiableSet(this.queued);
    }
}

