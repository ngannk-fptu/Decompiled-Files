/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span$Kind
 *  brave.internal.Nullable
 */
package brave.http;

import brave.Span;
import brave.http.HttpResponse;
import brave.http.HttpServerRequest;
import brave.internal.Nullable;

public abstract class HttpServerResponse
extends HttpResponse {
    public final Span.Kind spanKind() {
        return Span.Kind.SERVER;
    }

    @Override
    @Nullable
    public HttpServerRequest request() {
        return null;
    }

    public Throwable error() {
        return null;
    }
}

