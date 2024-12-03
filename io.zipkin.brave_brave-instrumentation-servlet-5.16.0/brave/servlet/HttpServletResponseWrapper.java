/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.http.HttpServerResponse
 *  brave.internal.Nullable
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package brave.servlet;

import brave.http.HttpServerResponse;
import brave.internal.Nullable;
import brave.servlet.HttpServletRequestWrapper;
import brave.servlet.internal.ServletRuntime;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseWrapper
extends HttpServerResponse {
    @Nullable
    final HttpServletRequestWrapper request;
    final HttpServletResponse response;
    @Nullable
    final Throwable caught;

    public static HttpServerResponse create(@Nullable HttpServletRequest request, HttpServletResponse response, @Nullable Throwable caught) {
        return new HttpServletResponseWrapper(request, response, caught);
    }

    HttpServletResponseWrapper(@Nullable HttpServletRequest request, HttpServletResponse response, @Nullable Throwable caught) {
        if (response == null) {
            throw new NullPointerException("response == null");
        }
        this.request = request != null ? new HttpServletRequestWrapper(request) : null;
        this.response = response;
        this.caught = caught;
    }

    public final Object unwrap() {
        return this.response;
    }

    @Nullable
    public HttpServletRequestWrapper request() {
        return this.request;
    }

    public Throwable error() {
        if (this.caught != null) {
            return this.caught;
        }
        if (this.request == null) {
            return null;
        }
        return this.request.maybeError();
    }

    public int statusCode() {
        int result = ServletRuntime.get().status(this.response);
        if (this.caught != null && result == 200) {
            if (this.caught instanceof UnavailableException) {
                return ((UnavailableException)this.caught).isPermanent() ? 404 : 503;
            }
            return 500;
        }
        return result;
    }
}

