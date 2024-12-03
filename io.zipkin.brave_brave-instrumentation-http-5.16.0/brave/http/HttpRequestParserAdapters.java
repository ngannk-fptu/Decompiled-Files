/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.SpanCustomizer
 *  brave.propagation.CurrentTraceContext
 *  brave.propagation.CurrentTraceContext$Scope
 *  brave.propagation.TraceContext
 */
package brave.http;

import brave.SpanCustomizer;
import brave.http.HttpAdapter;
import brave.http.HttpClientAdapters;
import brave.http.HttpClientRequest;
import brave.http.HttpHandler;
import brave.http.HttpParser;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.http.HttpServerAdapters;
import brave.http.HttpServerRequest;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;

@Deprecated
final class HttpRequestParserAdapters {
    HttpRequestParserAdapters() {
    }

    static abstract class HttpRequestParserAdapter
    implements HttpRequestParser {
        final CurrentTraceContext currentTraceContext;
        final HttpParser parser;

        HttpRequestParserAdapter(CurrentTraceContext currentTraceContext, HttpParser parser) {
            this.currentTraceContext = currentTraceContext;
            this.parser = parser;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        <Req> void parseInScope(TraceContext context, HttpAdapter<Req, ?> adapter, Req req, SpanCustomizer span) {
            CurrentTraceContext.Scope ws = this.currentTraceContext.maybeScope(context);
            try {
                this.parser.request(adapter, req, span);
            }
            finally {
                ws.close();
            }
        }
    }

    static final class ServerAdapter
    extends HttpRequestParserAdapter {
        ServerAdapter(CurrentTraceContext currentTraceContext, HttpParser parser) {
            super(currentTraceContext, parser);
        }

        @Override
        public void parse(HttpRequest request, TraceContext context, SpanCustomizer span) {
            Object req;
            HttpServerAdapters.ToRequestAdapter adapter;
            if (request instanceof HttpServerAdapters.FromRequestAdapter) {
                HttpServerAdapters.FromRequestAdapter wrapped = (HttpServerAdapters.FromRequestAdapter)request;
                adapter = wrapped.adapter;
                req = wrapped.request;
            } else if (request instanceof HttpServerRequest) {
                req = request.unwrap();
                if (req == null) {
                    req = HttpHandler.NULL_SENTINEL;
                }
                adapter = new HttpServerAdapters.ToRequestAdapter((HttpServerRequest)request, req);
            } else {
                throw new AssertionError((Object)"programming bug");
            }
            this.parseInScope(context, adapter, req, span);
        }
    }

    static final class ClientAdapter
    extends HttpRequestParserAdapter {
        ClientAdapter(CurrentTraceContext currentTraceContext, HttpParser parser) {
            super(currentTraceContext, parser);
        }

        @Override
        public void parse(HttpRequest request, TraceContext context, SpanCustomizer span) {
            Object req;
            HttpClientAdapters.ToRequestAdapter adapter;
            if (request instanceof HttpClientAdapters.FromRequestAdapter) {
                HttpClientAdapters.FromRequestAdapter wrapped = (HttpClientAdapters.FromRequestAdapter)request;
                adapter = wrapped.adapter;
                req = wrapped.request;
            } else if (request instanceof HttpClientRequest) {
                req = request.unwrap();
                if (req == null) {
                    req = HttpHandler.NULL_SENTINEL;
                }
                adapter = new HttpClientAdapters.ToRequestAdapter((HttpClientRequest)request, req);
            } else {
                throw new AssertionError((Object)"programming bug");
            }
            this.parseInScope(context, adapter, req, span);
        }
    }
}

