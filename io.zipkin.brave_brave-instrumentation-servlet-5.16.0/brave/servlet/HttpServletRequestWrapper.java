/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.http.HttpServerRequest
 *  brave.internal.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package brave.servlet;

import brave.Span;
import brave.http.HttpServerRequest;
import brave.internal.Nullable;
import javax.servlet.http.HttpServletRequest;

public final class HttpServletRequestWrapper
extends HttpServerRequest {
    final HttpServletRequest delegate;

    public static HttpServerRequest create(HttpServletRequest request) {
        return new HttpServletRequestWrapper(request);
    }

    HttpServletRequestWrapper(HttpServletRequest delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        this.delegate = delegate;
    }

    public final Object unwrap() {
        return this.delegate;
    }

    public boolean parseClientIpAndPort(Span span) {
        if (this.parseClientIpFromXForwardedFor(span)) {
            return true;
        }
        return span.remoteIpAndPort(this.delegate.getRemoteAddr(), this.delegate.getRemotePort());
    }

    public final String method() {
        return this.delegate.getMethod();
    }

    public String route() {
        Object maybeRoute = this.delegate.getAttribute("http.route");
        return maybeRoute instanceof String ? (String)maybeRoute : null;
    }

    public final String path() {
        return this.delegate.getRequestURI();
    }

    public String url() {
        StringBuffer url = this.delegate.getRequestURL();
        if (this.delegate.getQueryString() != null && !this.delegate.getQueryString().isEmpty()) {
            url.append('?').append(this.delegate.getQueryString());
        }
        return url.toString();
    }

    public final String header(String name) {
        return this.delegate.getHeader(name);
    }

    @Nullable
    Throwable maybeError() {
        Object maybeError = this.delegate.getAttribute("error");
        if (maybeError instanceof Throwable) {
            return (Throwable)maybeError;
        }
        maybeError = this.delegate.getAttribute("javax.servlet.error.exception");
        if (maybeError instanceof Throwable) {
            return (Throwable)maybeError;
        }
        return null;
    }
}

