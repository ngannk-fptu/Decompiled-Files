/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.util.concurrent.ExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.cache.AsynchronousAsyncValidator;
import org.apache.http.impl.client.cache.CachingHttpAsyncClient;

class AsynchronousAsyncValidationRequest
implements Runnable {
    private final AsynchronousAsyncValidator parent;
    private final CachingHttpAsyncClient cachingAsyncClient;
    private final HttpHost target;
    private final HttpRequestWrapper request;
    private final HttpCacheContext clientContext;
    private final HttpCacheEntry cacheEntry;
    private final String identifier;
    private final Log log = LogFactory.getLog(this.getClass());

    AsynchronousAsyncValidationRequest(AsynchronousAsyncValidator parent, CachingHttpAsyncClient cachingClient, HttpHost target, HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry cacheEntry, String identifier) {
        this.parent = parent;
        this.cachingAsyncClient = cachingClient;
        this.target = target;
        this.request = request;
        this.clientContext = clientContext;
        this.cacheEntry = cacheEntry;
        this.identifier = identifier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>(){

                @Override
                public void cancelled() {
                }

                @Override
                public void completed(HttpResponse httpResponse) {
                }

                @Override
                public void failed(Exception e) {
                    AsynchronousAsyncValidationRequest.this.log.debug("Asynchronous revalidation failed", e);
                }
            };
            BasicFuture<HttpResponse> future = new BasicFuture<HttpResponse>(callback);
            this.cachingAsyncClient.revalidateCacheEntry(future, this.target, this.request, this.clientContext, this.cacheEntry);
            future.get();
        }
        catch (ProtocolException pe) {
            this.log.error("ProtocolException thrown during asynchronous revalidation", pe);
        }
        catch (ExecutionException e) {
            this.log.error("Exception thrown during asynchronous revalidation", e.getCause());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            this.parent.markComplete(this.identifier);
        }
    }

    String getIdentifier() {
        return this.identifier;
    }
}

