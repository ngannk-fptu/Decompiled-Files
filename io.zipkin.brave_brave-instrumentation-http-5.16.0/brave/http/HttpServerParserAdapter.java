/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.ErrorParser
 *  brave.SpanCustomizer
 *  brave.propagation.CurrentTraceContext
 */
package brave.http;

import brave.ErrorParser;
import brave.SpanCustomizer;
import brave.http.HttpAdapter;
import brave.http.HttpRequestParser;
import brave.http.HttpResponseParser;
import brave.http.HttpServerAdapter;
import brave.http.HttpServerAdapters;
import brave.http.HttpServerParser;
import brave.http.HttpServerRequest;
import brave.http.HttpServerResponse;
import brave.propagation.CurrentTraceContext;

@Deprecated
final class HttpServerParserAdapter
extends HttpServerParser {
    final HttpRequestParser requestParser;
    final HttpResponseParser responseParser;
    final CurrentTraceContext currentTraceContext;
    final ErrorParser errorParser;

    HttpServerParserAdapter(HttpRequestParser requestParser, HttpResponseParser responseParser, CurrentTraceContext currentTraceContext, ErrorParser errorParser) {
        this.requestParser = requestParser;
        this.responseParser = responseParser;
        this.currentTraceContext = currentTraceContext;
        this.errorParser = errorParser;
    }

    @Override
    protected ErrorParser errorParser() {
        return this.errorParser;
    }

    @Override
    public <Req> void request(HttpAdapter<Req, ?> adapter, Req req, SpanCustomizer customizer) {
        HttpServerRequest request;
        if (req instanceof HttpServerRequest) {
            request = new HttpServerAdapters.FromRequestAdapter<Req>((HttpServerAdapter)adapter, req);
        } else if (adapter instanceof HttpServerAdapters.ToRequestAdapter) {
            request = ((HttpServerAdapters.ToRequestAdapter)adapter).delegate;
        } else {
            throw new AssertionError((Object)"programming bug");
        }
        this.requestParser.parse(request, this.currentTraceContext.get(), customizer);
    }

    @Override
    public <Resp> void response(HttpAdapter<?, Resp> adapter, Resp res, Throwable error, SpanCustomizer customizer) {
        HttpServerAdapters.FromResponseAdapter<Resp> response;
        if (res instanceof HttpServerResponse) {
            response = new HttpServerAdapters.FromResponseAdapter<Resp>((HttpServerAdapter)adapter, res, error);
        } else if (adapter instanceof HttpServerAdapters.ToResponseAdapter) {
            response = ((HttpServerAdapters.ToResponseAdapter)adapter).delegate;
        } else {
            throw new AssertionError((Object)"programming bug");
        }
        this.responseParser.parse(response, this.currentTraceContext.get(), customizer);
    }
}

