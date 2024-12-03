/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracing
 *  brave.http.HttpTracing
 *  org.apache.http.client.cache.CacheResponseStatus
 *  org.apache.http.impl.client.cache.CachingHttpClientBuilder
 *  org.apache.http.impl.execchain.ClientExecChain
 *  org.apache.http.protocol.HttpContext
 */
package brave.httpclient;

import brave.Span;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.httpclient.TracingMainExec;
import brave.httpclient.TracingProtocolExec;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.protocol.HttpContext;

public final class TracingCachingHttpClientBuilder
extends CachingHttpClientBuilder {
    final HttpTracing httpTracing;

    public static CachingHttpClientBuilder create(Tracing tracing) {
        return new TracingCachingHttpClientBuilder(HttpTracing.create((Tracing)tracing));
    }

    public static CachingHttpClientBuilder create(HttpTracing httpTracing) {
        return new TracingCachingHttpClientBuilder(httpTracing);
    }

    TracingCachingHttpClientBuilder(HttpTracing httpTracing) {
        if (httpTracing == null) {
            throw new NullPointerException("HttpTracing == null");
        }
        this.httpTracing = httpTracing;
    }

    protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
        return new TracingProtocolExec(this.httpTracing, protocolExec);
    }

    protected ClientExecChain decorateMainExec(ClientExecChain exec) {
        return new LocalIfFromCacheTracingMainExec(this.httpTracing, super.decorateMainExec(exec));
    }

    static final class LocalIfFromCacheTracingMainExec
    extends TracingMainExec {
        LocalIfFromCacheTracingMainExec(HttpTracing httpTracing, ClientExecChain mainExec) {
            super(httpTracing, mainExec);
        }

        @Override
        boolean isRemote(HttpContext context, Span span) {
            boolean cacheHit = CacheResponseStatus.CACHE_HIT.equals(context.getAttribute("http.cache.response.status"));
            if (cacheHit) {
                span.tag("http.cache_hit", "");
            }
            return !cacheHit;
        }
    }
}

