/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracer
 *  brave.internal.Nullable
 *  brave.propagation.TraceContext$Extractor
 *  brave.propagation.TraceContextOrSamplingFlags
 *  brave.sampler.SamplerFunction
 */
package brave.http;

import brave.Span;
import brave.Tracer;
import brave.http.HttpHandler;
import brave.http.HttpRequest;
import brave.http.HttpServerAdapter;
import brave.http.HttpServerAdapters;
import brave.http.HttpServerRequest;
import brave.http.HttpServerResponse;
import brave.http.HttpTracing;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.sampler.SamplerFunction;

public final class HttpServerHandler<Req, Resp>
extends HttpHandler {
    final Tracer tracer;
    final SamplerFunction<HttpRequest> sampler;
    @Deprecated
    @Nullable
    final HttpServerAdapter<Req, Resp> adapter;
    final TraceContext.Extractor<HttpServerRequest> defaultExtractor;

    public static HttpServerHandler<HttpServerRequest, HttpServerResponse> create(HttpTracing httpTracing) {
        if (httpTracing == null) {
            throw new NullPointerException("httpTracing == null");
        }
        return new HttpServerHandler<HttpServerRequest, HttpServerResponse>(httpTracing, null);
    }

    @Deprecated
    public static <Req, Resp> HttpServerHandler<Req, Resp> create(HttpTracing httpTracing, HttpServerAdapter<Req, Resp> adapter) {
        if (httpTracing == null) {
            throw new NullPointerException("httpTracing == null");
        }
        if (adapter == null) {
            throw new NullPointerException("adapter == null");
        }
        return new HttpServerHandler<Req, Resp>(httpTracing, adapter);
    }

    HttpServerHandler(HttpTracing httpTracing, @Deprecated HttpServerAdapter<Req, Resp> adapter) {
        super(httpTracing.serverRequestParser(), httpTracing.serverResponseParser());
        this.adapter = adapter;
        this.tracer = httpTracing.tracing().tracer();
        this.sampler = httpTracing.serverRequestSampler();
        this.defaultExtractor = httpTracing.propagation().extractor(HttpServerRequest.GETTER);
    }

    public Span handleReceive(HttpServerRequest request) {
        Span span = this.nextSpan(this.defaultExtractor.extract((Object)request), request);
        return this.handleStart(request, span);
    }

    @Deprecated
    public Span handleReceive(TraceContext.Extractor<Req> extractor, Req request) {
        return this.handleReceive(extractor, request, request);
    }

    @Deprecated
    public <C> Span handleReceive(TraceContext.Extractor<C> extractor, C carrier, Req request) {
        if (carrier == null) {
            throw new NullPointerException("request == null");
        }
        HttpServerAdapters.FromRequestAdapter<Req> serverRequest = request instanceof HttpServerRequest ? (HttpServerAdapters.FromRequestAdapter<Req>)((Object)request) : new HttpServerAdapters.FromRequestAdapter<Req>(this.adapter, request);
        Span span = this.nextSpan(extractor.extract(carrier), serverRequest);
        return this.handleStart(serverRequest, span);
    }

    @Override
    void parseRequest(HttpRequest request, Span span) {
        ((HttpServerRequest)request).parseClientIpAndPort(span);
        super.parseRequest(request, span);
    }

    Span nextSpan(TraceContextOrSamplingFlags extracted, HttpServerRequest request) {
        Boolean sampled = extracted.sampled();
        if (sampled == null && (sampled = this.sampler.trySample((Object)request)) != null) {
            extracted = extracted.sampled(sampled.booleanValue());
        }
        return extracted.context() != null ? this.tracer.joinSpan(extracted.context()) : this.tracer.nextSpan(extracted);
    }

    public void handleSend(@Nullable Resp response, @Nullable Throwable error, Span span) {
        HttpServerAdapters.FromResponseAdapter<Resp> serverResponse;
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
        if (response instanceof HttpServerResponse) {
            serverResponse = (HttpServerAdapters.FromResponseAdapter<Resp>)((Object)response);
            if (((HttpServerResponse)serverResponse).error() == null && error != null) {
                span.error(error);
            }
        } else {
            serverResponse = new HttpServerAdapters.FromResponseAdapter<Resp>(this.adapter, response, error);
        }
        this.handleFinish(serverResponse, span);
    }

    public void handleSend(HttpServerResponse response, Span span) {
        this.handleFinish(response, span);
    }
}

