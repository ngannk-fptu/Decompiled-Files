/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpFields$Mutable
 *  org.eclipse.jetty.http.HttpVersion
 */
package org.eclipse.jetty.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;

public class HttpResponse
implements Response {
    private final HttpFields.Mutable headers = HttpFields.build();
    private final Request request;
    private final List<Response.ResponseListener> listeners;
    private HttpVersion version;
    private int status;
    private String reason;
    private HttpFields.Mutable trailers;

    public HttpResponse(Request request, List<Response.ResponseListener> listeners) {
        this.request = request;
        this.listeners = listeners;
    }

    @Override
    public Request getRequest() {
        return this.request;
    }

    @Override
    public HttpVersion getVersion() {
        return this.version;
    }

    public HttpResponse version(HttpVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    public HttpResponse status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    public HttpResponse reason(String reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public HttpFields getHeaders() {
        return this.headers.asImmutable();
    }

    public void clearHeaders() {
        this.headers.clear();
    }

    public HttpResponse addHeader(HttpField header) {
        this.headers.add(header);
        return this;
    }

    public HttpResponse headers(Consumer<HttpFields.Mutable> consumer) {
        consumer.accept(this.headers);
        return this;
    }

    @Override
    public <T extends Response.ResponseListener> List<T> getListeners(Class<T> type) {
        ArrayList<Response.ResponseListener> result = new ArrayList<Response.ResponseListener>();
        for (Response.ResponseListener listener : this.listeners) {
            if (type != null && !type.isInstance(listener)) continue;
            result.add(listener);
        }
        return result;
    }

    public HttpFields getTrailers() {
        return this.trailers == null ? null : this.trailers.asImmutable();
    }

    public HttpResponse trailer(HttpField trailer) {
        if (this.trailers == null) {
            this.trailers = HttpFields.build();
        }
        this.trailers.add(trailer);
        return this;
    }

    @Override
    public boolean abort(Throwable cause) {
        return this.request.abort(cause);
    }

    public String toString() {
        return String.format("%s[%s %d %s]@%x", HttpResponse.class.getSimpleName(), this.getVersion(), this.getStatus(), this.getReason(), this.hashCode());
    }
}

