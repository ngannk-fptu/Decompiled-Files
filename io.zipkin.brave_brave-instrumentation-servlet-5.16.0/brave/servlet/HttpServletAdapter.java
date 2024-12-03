/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.http.HttpServerAdapter
 *  brave.internal.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package brave.servlet;

import brave.Span;
import brave.http.HttpServerAdapter;
import brave.internal.Nullable;
import brave.servlet.internal.ServletRuntime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

@Deprecated
public class HttpServletAdapter
extends HttpServerAdapter<HttpServletRequest, HttpServletResponse> {
    final ServletRuntime servlet = ServletRuntime.get();

    public HttpServletResponse adaptResponse(HttpServletRequest req, HttpServletResponse resp) {
        Object maybeRoute = req.getAttribute("http.route");
        return maybeRoute instanceof String ? new DecoratedHttpServletResponse(resp, req.getMethod(), (String)maybeRoute) : resp;
    }

    public boolean parseClientIpAndPort(HttpServletRequest req, Span span) {
        if (this.parseClientIpFromXForwardedFor(req, span)) {
            return true;
        }
        return span.remoteIpAndPort(req.getRemoteAddr(), req.getRemotePort());
    }

    public String method(HttpServletRequest request) {
        return request.getMethod();
    }

    public String path(HttpServletRequest request) {
        return request.getRequestURI();
    }

    public String url(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            url.append('?').append(request.getQueryString());
        }
        return url.toString();
    }

    public String requestHeader(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }

    public String methodFromResponse(HttpServletResponse response) {
        if (response instanceof DecoratedHttpServletResponse) {
            return ((DecoratedHttpServletResponse)response).method;
        }
        return null;
    }

    public String route(HttpServletResponse response) {
        if (response instanceof DecoratedHttpServletResponse) {
            return ((DecoratedHttpServletResponse)response).httpRoute;
        }
        return null;
    }

    @Nullable
    public Integer statusCode(HttpServletResponse response) {
        int result = this.statusCodeAsInt(response);
        return result != 0 ? Integer.valueOf(result) : null;
    }

    public int statusCodeAsInt(HttpServletResponse response) {
        return this.servlet.status(response);
    }

    static class DecoratedHttpServletResponse
    extends HttpServletResponseWrapper {
        final String method;
        final String httpRoute;

        DecoratedHttpServletResponse(HttpServletResponse response, String method, String httpRoute) {
            super(response);
            this.method = method;
            this.httpRoute = httpRoute;
        }
    }
}

