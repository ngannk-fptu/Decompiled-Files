/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.SpanCustomizer
 *  brave.internal.Nullable
 *  brave.propagation.CurrentTraceContext
 *  brave.propagation.CurrentTraceContext$Scope
 *  brave.propagation.TraceContext
 */
package brave.http;

import brave.SpanCustomizer;
import brave.http.HttpAdapter;
import brave.http.HttpClientAdapters;
import brave.http.HttpClientResponse;
import brave.http.HttpHandler;
import brave.http.HttpParser;
import brave.http.HttpResponse;
import brave.http.HttpResponseParser;
import brave.http.HttpServerAdapters;
import brave.http.HttpServerResponse;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;

@Deprecated
final class HttpResponseParserAdapters {
    HttpResponseParserAdapters() {
    }

    static abstract class HttpResponseParserAdapter
    implements HttpResponseParser {
        final CurrentTraceContext currentTraceContext;
        final HttpParser parser;

        HttpResponseParserAdapter(CurrentTraceContext currentTraceContext, HttpParser parser) {
            this.currentTraceContext = currentTraceContext;
            this.parser = parser;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        <Resp> void parseInScope(TraceContext context, HttpAdapter<?, Resp> adapter, @Nullable Resp res, @Nullable Throwable error, SpanCustomizer customizer) {
            CurrentTraceContext.Scope ws = this.currentTraceContext.maybeScope(context);
            try {
                this.parser.response(adapter, res, error, customizer);
            }
            finally {
                ws.close();
            }
        }
    }

    static final class ServerAdapter
    extends HttpResponseParserAdapter {
        ServerAdapter(CurrentTraceContext currentTraceContext, HttpParser parser) {
            super(currentTraceContext, parser);
        }

        @Override
        public void parse(HttpResponse response, TraceContext context, SpanCustomizer span) {
            Object res;
            HttpServerAdapters.ToResponseAdapter adapter;
            if (response instanceof HttpServerAdapters.FromResponseAdapter) {
                HttpServerAdapters.FromResponseAdapter wrapped = (HttpServerAdapters.FromResponseAdapter)response;
                adapter = wrapped.adapter;
                res = wrapped.response;
            } else if (response instanceof HttpServerResponse) {
                res = response.unwrap();
                if (res == null) {
                    res = HttpHandler.NULL_SENTINEL;
                }
                adapter = new HttpServerAdapters.ToResponseAdapter((HttpServerResponse)response, res);
            } else {
                throw new AssertionError((Object)"programming bug");
            }
            this.parseInScope(context, adapter, res, response.error(), span);
        }
    }

    static final class ClientAdapter
    extends HttpResponseParserAdapter {
        ClientAdapter(CurrentTraceContext currentTraceContext, HttpParser parser) {
            super(currentTraceContext, parser);
        }

        @Override
        public void parse(HttpResponse response, TraceContext context, SpanCustomizer span) {
            Object res;
            HttpClientAdapters.ToResponseAdapter adapter;
            if (response instanceof HttpClientAdapters.FromResponseAdapter) {
                HttpClientAdapters.FromResponseAdapter wrapped = (HttpClientAdapters.FromResponseAdapter)response;
                adapter = wrapped.adapter;
                res = wrapped.response;
            } else if (response instanceof HttpClientResponse) {
                res = response.unwrap();
                if (res == null) {
                    res = HttpHandler.NULL_SENTINEL;
                }
                adapter = new HttpClientAdapters.ToResponseAdapter((HttpClientResponse)response, res);
            } else {
                throw new AssertionError((Object)"programming bug");
            }
            this.parseInScope(context, adapter, res, response.error(), span);
        }
    }
}

