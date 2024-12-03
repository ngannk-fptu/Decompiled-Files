/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.SpanCustomizer
 *  brave.Tracing
 *  brave.http.HttpServerHandler
 *  brave.http.HttpServerRequest
 *  brave.http.HttpServerResponse
 *  brave.http.HttpTracing
 *  brave.propagation.CurrentTraceContext
 *  brave.propagation.CurrentTraceContext$Scope
 *  brave.propagation.TraceContext
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package brave.servlet;

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracing;
import brave.http.HttpServerHandler;
import brave.http.HttpServerRequest;
import brave.http.HttpServerResponse;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import brave.servlet.HttpServletRequestWrapper;
import brave.servlet.HttpServletResponseWrapper;
import brave.servlet.internal.ServletRuntime;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class TracingFilter
implements Filter {
    final ServletRuntime servlet = ServletRuntime.get();
    final CurrentTraceContext currentTraceContext;
    final HttpServerHandler<HttpServerRequest, HttpServerResponse> handler;

    public static Filter create(Tracing tracing) {
        return new TracingFilter(HttpTracing.create((Tracing)tracing));
    }

    public static Filter create(HttpTracing httpTracing) {
        return new TracingFilter(httpTracing);
    }

    TracingFilter(HttpTracing httpTracing) {
        this.currentTraceContext = httpTracing.tracing().currentTraceContext();
        this.handler = HttpServerHandler.create((HttpTracing)httpTracing);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = this.servlet.httpServletResponse(response);
        TraceContext context = (TraceContext)request.getAttribute(TraceContext.class.getName());
        if (context != null) {
            CurrentTraceContext.Scope scope = this.currentTraceContext.maybeScope(context);
            try {
                chain.doFilter(request, response);
            }
            finally {
                scope.close();
            }
            return;
        }
        Span span = this.handler.handleReceive((HttpServerRequest)new HttpServletRequestWrapper(req));
        request.setAttribute(SpanCustomizer.class.getName(), (Object)span.customizer());
        request.setAttribute(TraceContext.class.getName(), (Object)span.context());
        SendHandled sendHandled = new SendHandled();
        request.setAttribute(SendHandled.class.getName(), (Object)sendHandled);
        Throwable error = null;
        CurrentTraceContext.Scope scope = this.currentTraceContext.newScope(span.context());
        try {
            chain.doFilter((ServletRequest)req, (ServletResponse)res);
        }
        catch (Throwable e) {
            error = e;
            throw e;
        }
        finally {
            if (this.servlet.isAsync(req)) {
                this.servlet.handleAsync(this.handler, req, res, span);
            } else if (sendHandled.compareAndSet(false, true)) {
                HttpServerResponse responseWrapper = HttpServletResponseWrapper.create(req, res, error);
                this.handler.handleSend(responseWrapper, span);
            }
            scope.close();
        }
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
    }

    static final class SendHandled
    extends AtomicBoolean {
        SendHandled() {
        }
    }
}

