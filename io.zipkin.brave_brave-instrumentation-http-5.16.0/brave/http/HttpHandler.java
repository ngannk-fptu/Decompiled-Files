/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.internal.Platform
 *  brave.internal.Throwables
 */
package brave.http;

import brave.Span;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.http.HttpResponse;
import brave.http.HttpResponseParser;
import brave.internal.Platform;
import brave.internal.Throwables;

abstract class HttpHandler {
    static final Object NULL_SENTINEL = new Object();
    final HttpRequestParser requestParser;
    final HttpResponseParser responseParser;

    HttpHandler(HttpRequestParser requestParser, HttpResponseParser responseParser) {
        this.requestParser = requestParser;
        this.responseParser = responseParser;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Span handleStart(HttpRequest request, Span span) {
        if (span.isNoop()) {
            return span;
        }
        try {
            this.parseRequest(request, span);
        }
        catch (Throwable t) {
            Throwables.propagateIfFatal((Throwable)t);
            Platform.get().log("error parsing request {0}", (Object)request, t);
        }
        finally {
            long timestamp = request.startTimestamp();
            if (timestamp == 0L) {
                span.start();
            } else {
                span.start(timestamp);
            }
        }
        return span;
    }

    void parseRequest(HttpRequest request, Span span) {
        span.kind(request.spanKind());
        this.requestParser.parse(request, span.context(), span.customizer());
    }

    void parseResponse(HttpResponse response, Span span) {
        this.responseParser.parse(response, span.context(), span.customizer());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void handleFinish(HttpResponse response, Span span) {
        if (response == null) {
            throw new NullPointerException("response == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        if (span.isNoop()) {
            return;
        }
        if (response.error() != null) {
            span.error(response.error());
        }
        try {
            this.parseResponse(response, span);
        }
        catch (Throwable t) {
            Throwables.propagateIfFatal((Throwable)t);
            Platform.get().log("error parsing response {0}", (Object)response, t);
        }
        finally {
            long finishTimestamp = response.finishTimestamp();
            if (finishTimestamp == 0L) {
                span.finish();
            } else {
                span.finish(finishTimestamp);
            }
        }
    }
}

