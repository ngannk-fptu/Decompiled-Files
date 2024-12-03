/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span$Kind
 *  brave.propagation.Propagation$RemoteSetter
 */
package brave.http;

import brave.Span;
import brave.http.HttpRequest;
import brave.propagation.Propagation;

public abstract class HttpClientRequest
extends HttpRequest {
    static final Propagation.RemoteSetter<HttpClientRequest> SETTER = new Propagation.RemoteSetter<HttpClientRequest>(){

        public Span.Kind spanKind() {
            return Span.Kind.CLIENT;
        }

        public void put(HttpClientRequest request, String key, String value) {
            request.header(key, value);
        }

        public String toString() {
            return "HttpClientRequest::header";
        }
    };

    public final Span.Kind spanKind() {
        return Span.Kind.CLIENT;
    }

    public abstract void header(String var1, String var2);
}

