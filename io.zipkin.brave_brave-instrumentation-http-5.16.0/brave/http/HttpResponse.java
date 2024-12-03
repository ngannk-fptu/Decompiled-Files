/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Response
 *  brave.internal.Nullable
 */
package brave.http;

import brave.Response;
import brave.http.HttpRequest;
import brave.internal.Nullable;

public abstract class HttpResponse
extends Response {
    @Nullable
    public HttpRequest request() {
        return null;
    }

    @Nullable
    public String method() {
        HttpRequest request = this.request();
        return request != null ? request.method() : null;
    }

    @Nullable
    public String route() {
        HttpRequest request = this.request();
        return request != null ? request.route() : null;
    }

    public abstract int statusCode();

    public long finishTimestamp() {
        return 0L;
    }

    HttpResponse() {
    }
}

