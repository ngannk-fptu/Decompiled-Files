/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  zipkin2.Endpoint$Builder
 */
package brave.http;

import brave.Span;
import brave.http.HttpAdapter;
import zipkin2.Endpoint;

@Deprecated
public abstract class HttpServerAdapter<Req, Resp>
extends HttpAdapter<Req, Resp> {
    @Deprecated
    public boolean parseClientAddress(Req req, Endpoint.Builder builder) {
        return false;
    }

    public boolean parseClientIpAndPort(Req req, Span span) {
        return this.parseClientIpFromXForwardedFor(req, span);
    }

    public boolean parseClientIpFromXForwardedFor(Req req, Span span) {
        String forwardedFor = this.requestHeader(req, "X-Forwarded-For");
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

