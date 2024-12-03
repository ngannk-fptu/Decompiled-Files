/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracer$SpanInScope
 *  brave.Tracing
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache$RequestCache
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  org.slf4j.MDC
 */
package com.atlassian.confluence.api.impl.sal;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.MDC;

public class ConfluenceThreadLocalContextManager
implements ThreadLocalContextManager<ThreadLocalContext> {
    private final Tracing tracing;
    private volatile ScopesRequestCacheDelegate scopesRequestCacheDelegate;
    private final ThreadLocal<Tracer.SpanInScope> currentSpanInScope = new ThreadLocal();

    public ConfluenceThreadLocalContextManager(Tracing tracing) {
        this.tracing = tracing;
    }

    public ThreadLocalContext getThreadLocalContext() {
        return new ThreadLocalContext(AuthenticatedUserThreadLocal.get(), new HashMap<String, Object>(RequestCacheThreadLocal.getRequestCache()), MDC.getCopyOfContextMap(), this.tracing.tracer().currentSpan(), this.getScopesRequestCacheDelegate().flatMap(ScopesRequestCacheDelegate::getRequestCache).orElse(new ScopesRequestCache.RequestCache()));
    }

    public void setThreadLocalContext(ThreadLocalContext context) {
        AuthenticatedUserThreadLocal.set(context.user);
        RequestCacheThreadLocal.setRequestCache(context.requestCache);
        MDC.setContextMap(context.mdcContext);
        if (!Objects.equals(this.tracing.tracer().currentSpan(), context.span)) {
            Tracer.SpanInScope oldSpanInScope = this.currentSpanInScope.get();
            if (oldSpanInScope != null) {
                oldSpanInScope.close();
            }
            Tracer.SpanInScope contextSpanInScope = this.tracing.tracer().withSpanInScope(context.span);
            this.currentSpanInScope.set(contextSpanInScope);
        }
        this.getScopesRequestCacheDelegate().ifPresent(scopesRequestCacheDelegate -> scopesRequestCacheDelegate.setRequestCache(context.scopesRequestCache));
    }

    public void clearThreadLocalContext() {
        AuthenticatedUserThreadLocal.reset();
        RequestCacheThreadLocal.clearRequestCache();
        MDC.clear();
        Tracer.SpanInScope oldSpanInScope = this.currentSpanInScope.get();
        if (oldSpanInScope != null) {
            oldSpanInScope.close();
        }
        this.currentSpanInScope.remove();
        this.getScopesRequestCacheDelegate().ifPresent(ScopesRequestCacheDelegate::clearRequestCache);
    }

    public void setScopesRequestCacheDelegate(ScopesRequestCacheDelegate scopesRequestCacheDelegate) {
        if (this.scopesRequestCacheDelegate == null) {
            this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
        }
    }

    private Optional<ScopesRequestCacheDelegate> getScopesRequestCacheDelegate() {
        return Optional.ofNullable(this.scopesRequestCacheDelegate);
    }

    static final class ThreadLocalContext {
        final ConfluenceUser user;
        final Map<String, Object> requestCache;
        final Map<String, String> mdcContext;
        final Span span;
        final ScopesRequestCache.RequestCache scopesRequestCache;

        ThreadLocalContext(ConfluenceUser user, Map<String, Object> requestCache, Map<String, String> mdcContext, Span span, ScopesRequestCache.RequestCache scopesRequestCache) {
            this.user = user;
            this.requestCache = requestCache;
            this.mdcContext = mdcContext != null ? mdcContext : Collections.emptyMap();
            this.span = span;
            this.scopesRequestCache = scopesRequestCache;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ThreadLocalContext that = (ThreadLocalContext)o;
            return Objects.equals(this.user, that.user) && Objects.equals(this.requestCache, that.requestCache) && Objects.equals(this.mdcContext, that.mdcContext) && Objects.equals(this.span, that.span) && Objects.equals(this.scopesRequestCache, that.scopesRequestCache);
        }

        public int hashCode() {
            return Objects.hash(this.user, this.requestCache, this.mdcContext, this.span, this.scopesRequestCache);
        }
    }
}

