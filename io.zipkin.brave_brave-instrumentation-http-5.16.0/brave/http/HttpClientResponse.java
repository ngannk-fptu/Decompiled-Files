/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span$Kind
 *  brave.internal.Nullable
 */
package brave.http;

import brave.Span;
import brave.http.HttpClientRequest;
import brave.http.HttpResponse;
import brave.internal.Nullable;

public abstract class HttpClientResponse
extends HttpResponse {
    public final Span.Kind spanKind() {
        return Span.Kind.CLIENT;
    }

    @Override
    @Nullable
    public HttpClientRequest request() {
        return null;
    }

    public Throwable error() {
        return null;
    }
}

