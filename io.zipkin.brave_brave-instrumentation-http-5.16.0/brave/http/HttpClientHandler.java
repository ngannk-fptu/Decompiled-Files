/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracer
 *  brave.internal.Nullable
 *  brave.propagation.TraceContext
 *  brave.propagation.TraceContext$Injector
 *  brave.sampler.Sampler
 *  brave.sampler.SamplerFunction
 */
package brave.http;

import brave.Span;
import brave.Tracer;
import brave.http.HttpClientAdapter;
import brave.http.HttpClientAdapters;
import brave.http.HttpClientRequest;
import brave.http.HttpClientResponse;
import brave.http.HttpHandler;
import brave.http.HttpRequest;
import brave.http.HttpTracing;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import brave.sampler.Sampler;
import brave.sampler.SamplerFunction;

public final class HttpClientHandler<Req, Resp>
extends HttpHandler {
    final Tracer tracer;
    @Deprecated
    @Nullable
    final HttpClientAdapter<Req, Resp> adapter;
    final Sampler sampler;
    final SamplerFunction<HttpRequest> httpSampler;
    @Nullable
    final String serverName;
    final TraceContext.Injector<HttpClientRequest> defaultInjector;

    public static HttpClientHandler<HttpClientRequest, HttpClientResponse> create(HttpTracing httpTracing) {
        if (httpTracing == null) {
            throw new NullPointerException("httpTracing == null");
        }
        return new HttpClientHandler<HttpClientRequest, HttpClientResponse>(httpTracing, null);
    }

    @Deprecated
    public static <Req, Resp> HttpClientHandler<Req, Resp> create(HttpTracing httpTracing, HttpClientAdapter<Req, Resp> adapter) {
        if (httpTracing == null) {
            throw new NullPointerException("httpTracing == null");
        }
        if (adapter == null) {
            throw new NullPointerException("adapter == null");
        }
        return new HttpClientHandler<Req, Resp>(httpTracing, adapter);
    }

    HttpClientHandler(HttpTracing httpTracing, @Deprecated HttpClientAdapter<Req, Resp> adapter) {
        super(httpTracing.clientRequestParser(), httpTracing.clientResponseParser());
        this.adapter = adapter;
        this.tracer = httpTracing.tracing().tracer();
        this.sampler = httpTracing.tracing().sampler();
        this.httpSampler = httpTracing.clientRequestSampler();
        this.serverName = !"".equals(httpTracing.serverName()) ? httpTracing.serverName() : null;
        this.defaultInjector = httpTracing.propagation().injector(HttpClientRequest.SETTER);
    }

    public Span handleSend(HttpClientRequest request) {
        if (request == null) {
            throw new NullPointerException("request == null");
        }
        return this.handleSend(request, this.tracer.nextSpan(this.httpSampler, (Object)request));
    }

    public Span handleSendWithParent(HttpClientRequest request, @Nullable TraceContext parent) {
        if (request == null) {
            throw new NullPointerException("request == null");
        }
        return this.handleSend(request, this.tracer.nextSpanWithParent(this.httpSampler, (Object)request, parent));
    }

    public Span handleSend(HttpClientRequest request, Span span) {
        if (request == null) {
            throw new NullPointerException("request == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        this.defaultInjector.inject(span.context(), (Object)request);
        return this.handleStart(request, span);
    }

    @Override
    void parseRequest(HttpRequest request, Span span) {
        if (this.serverName != null) {
            span.remoteServiceName(this.serverName);
        }
        super.parseRequest(request, span);
    }

    @Deprecated
    public Span handleSend(TraceContext.Injector<Req> injector, Req request) {
        return this.handleSend(injector, request, request);
    }

    @Deprecated
    public <C> Span handleSend(TraceContext.Injector<C> injector, C carrier, Req request) {
        return this.handleSend(injector, carrier, request, this.nextSpan(request));
    }

    @Deprecated
    public Span handleSend(TraceContext.Injector<Req> injector, Req request, Span span) {
        return this.handleSend(injector, request, request, span);
    }

    @Deprecated
    public <C> Span handleSend(TraceContext.Injector<C> injector, C carrier, Req request, Span span) {
        if (request == null) {
            throw new NullPointerException("carrier == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        injector.inject(span.context(), carrier);
        HttpClientAdapters.FromRequestAdapter<Req> clientRequest = request instanceof HttpClientRequest ? (HttpClientAdapters.FromRequestAdapter<Req>)((Object)request) : new HttpClientAdapters.FromRequestAdapter<Req>(this.adapter, request);
        return this.handleStart(clientRequest, span);
    }

    @Deprecated
    public Span nextSpan(Req request) {
        HttpClientAdapters.FromRequestAdapter<Req> clientRequest = request instanceof HttpClientRequest ? (HttpClientAdapters.FromRequestAdapter<Req>)((Object)request) : new HttpClientAdapters.FromRequestAdapter<Req>(this.adapter, request);
        return this.tracer.nextSpan(this.httpSampler, clientRequest);
    }

    @Deprecated
    public void handleReceive(@Nullable Resp response, @Nullable Throwable error, Span span) {
        HttpClientAdapters.FromResponseAdapter<Resp> clientResponse;
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        if (response == null && error == null) {
            throw new IllegalArgumentException("Either the response or error parameters may be null, but not both");
        }
        if (response == null) {
            span.error(error).finish();
            return;
        }
        if (response instanceof HttpClientResponse) {
            clientResponse = (HttpClientAdapters.FromResponseAdapter<Resp>)((Object)response);
            if (((HttpClientResponse)clientResponse).error() == null && error != null) {
                span.error(error);
            }
        } else {
            clientResponse = new HttpClientAdapters.FromResponseAdapter<Resp>(this.adapter, response, error);
        }
        this.handleFinish(clientResponse, span);
    }

    public void handleReceive(HttpClientResponse response, Span span) {
        this.handleFinish(response, span);
    }
}

