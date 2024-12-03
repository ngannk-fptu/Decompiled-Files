/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Span$Kind
 *  brave.propagation.Propagation$RemoteGetter
 */
package brave.http;

import brave.Span;
import brave.http.HttpRequest;
import brave.propagation.Propagation;

public abstract class HttpServerRequest
extends HttpRequest {
    static final Propagation.RemoteGetter<HttpServerRequest> GETTER = new Propagation.RemoteGetter<HttpServerRequest>(){

        public Span.Kind spanKind() {
            return Span.Kind.SERVER;
        }

        public String get(HttpServerRequest request, String key) {
            return request.header(key);
        }

        public String toString() {
            return "HttpServerRequest::header";
        }
    };

    public final Span.Kind spanKind() {
        return Span.Kind.SERVER;
    }

    public boolean parseClientIpAndPort(Span span) {
        return this.parseClientIpFromXForwardedFor(span);
    }

    protected boolean parseClientIpFromXForwardedFor(Span span) {
        String forwardedFor = this.header("X-Forwarded-For");
        if (forwardedFor == null) {
            return false;
        }
        int indexOfComma = forwardedFor.indexOf(44);
        if (indexOfComma != -1) {
            forwardedFor = forwardedFor.substring(0, indexOfComma);
        }
        return span.remoteIpAndPort(forwardedFor, 0);
    }
}

