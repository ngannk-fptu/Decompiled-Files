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
import brave.http.HttpClientAdapter;
import brave.http.HttpClientAdapters;
import brave.http.HttpClientParser;
import brave.http.HttpClientRequest;
import brave.http.HttpClientResponse;
import brave.http.HttpRequestParser;
import brave.http.HttpResponseParser;
import brave.propagation.CurrentTraceContext;

@Deprecated
final class HttpClientParserAdapter
extends HttpClientParser {
    final HttpRequestParser requestParser;
    final HttpResponseParser responseParser;
    final CurrentTraceContext currentTraceContext;
    final ErrorParser errorParser;

    HttpClientParserAdapter(HttpRequestParser requestParser, HttpResponseParser responseParser, CurrentTraceContext currentTraceContext, ErrorParser errorParser) {
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
        HttpClientRequest request;
        if (req instanceof HttpClientRequest) {
            request = new HttpClientAdapters.FromRequestAdapter<Req>((HttpClientAdapter)adapter, req);
        } else if (adapter instanceof HttpClientAdapters.ToRequestAdapter) {
            request = ((HttpClientAdapters.ToRequestAdapter)adapter).delegate;
        } else {
            throw new AssertionError((Object)"programming bug");
        }
        this.requestParser.parse(request, this.currentTraceContext.get(), customizer);
    }

    @Override
    public <Resp> void response(HttpAdapter<?, Resp> adapter, Resp res, Throwable error, SpanCustomizer customizer) {
        HttpClientAdapters.FromResponseAdapter<Resp> response;
        if (res instanceof HttpClientResponse) {
            response = new HttpClientAdapters.FromResponseAdapter<Resp>((HttpClientAdapter)adapter, res, error);
        } else if (adapter instanceof HttpClientAdapters.ToResponseAdapter) {
            response = ((HttpClientAdapters.ToResponseAdapter)adapter).delegate;
        } else {
            throw new AssertionError((Object)"programming bug");
        }
        this.responseParser.parse(response, this.currentTraceContext.get(), customizer);
    }
}

