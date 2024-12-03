/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.cache.AsynchronousValidator;
import org.apache.http.impl.client.cache.CachingExec;

public class AsynchronousValidationRequest
implements Runnable {
    private final AsynchronousValidator parent;
    private final CachingExec cachingExec;
    private final HttpRoute route;
    private final HttpRequestWrapper request;
    private final HttpClientContext context;
    private final HttpExecutionAware execAware;
    private final HttpCacheEntry cacheEntry;
    private final String identifier;
    private final int consecutiveFailedAttempts;
    private final Log log = LogFactory.getLog(this.getClass());

    AsynchronousValidationRequest(AsynchronousValidator parent, CachingExec cachingExec, HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, HttpCacheEntry cacheEntry, String identifier, int consecutiveFailedAttempts) {
        this.parent = parent;
        this.cachingExec = cachingExec;
        this.route = route;
        this.request = request;
        this.context = context;
        this.execAware = execAware;
        this.cacheEntry = cacheEntry;
        this.identifier = identifier;
        this.consecutiveFailedAttempts = consecutiveFailedAttempts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            if (this.revalidateCacheEntry()) {
                this.parent.jobSuccessful(this.identifier);
            } else {
                this.parent.jobFailed(this.identifier);
            }
        }
        finally {
            this.parent.markComplete(this.identifier);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean revalidateCacheEntry() {
        boolean bl;
        CloseableHttpResponse httpResponse = this.cachingExec.revalidateCacheEntry(this.route, this.request, this.context, this.execAware, this.cacheEntry);
        try {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            bl = this.isNotServerError(statusCode) && this.isNotStale(httpResponse);
        }
        catch (Throwable throwable) {
            try {
                httpResponse.close();
                throw throwable;
            }
            catch (IOException ioe) {
                this.log.debug("Asynchronous revalidation failed due to I/O error", ioe);
                return false;
            }
            catch (HttpException pe) {
                this.log.error("HTTP protocol exception during asynchronous revalidation", pe);
                return false;
            }
            catch (RuntimeException re) {
                this.log.error("RuntimeException thrown during asynchronous revalidation: " + re);
                return false;
            }
        }
        httpResponse.close();
        return bl;
    }

    private boolean isNotServerError(int statusCode) {
        return statusCode < 500;
    }

    private boolean isNotStale(HttpResponse httpResponse) {
        Header[] warnings = httpResponse.getHeaders("Warning");
        if (warnings != null) {
            for (Header warning : warnings) {
                String warningValue = warning.getValue();
                if (!warningValue.startsWith("110") && !warningValue.startsWith("111")) continue;
                return false;
            }
        }
        return true;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getConsecutiveFailedAttempts() {
        return this.consecutiveFailedAttempts;
    }
}

