/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span$Kind
 *  brave.internal.Nullable
 *  brave.sampler.SamplerFunction
 *  brave.sampler.SamplerFunctions
 */
package brave.http;

import brave.Span;
import brave.http.HttpAdapter;
import brave.http.HttpClientAdapters;
import brave.http.HttpClientRequest;
import brave.http.HttpHandler;
import brave.http.HttpRequest;
import brave.http.HttpServerAdapter;
import brave.http.HttpServerAdapters;
import brave.http.HttpServerRequest;
import brave.internal.Nullable;
import brave.sampler.SamplerFunction;
import brave.sampler.SamplerFunctions;

@Deprecated
public abstract class HttpSampler
implements SamplerFunction<HttpRequest> {
    public static final HttpSampler TRACE_ID = new HttpSampler(){

        @Override
        public Boolean trySample(HttpRequest request) {
            return null;
        }

        @Override
        @Nullable
        public <Req> Boolean trySample(HttpAdapter<Req, ?> adapter, Req request) {
            return null;
        }

        public String toString() {
            return "DeferDecision";
        }
    };
    public static final HttpSampler NEVER_SAMPLE = new HttpSampler(){

        @Override
        public Boolean trySample(HttpRequest request) {
            return false;
        }

        @Override
        public <Req> Boolean trySample(HttpAdapter<Req, ?> adapter, Req request) {
            return false;
        }

        public String toString() {
            return "NeverSample";
        }
    };

    @Nullable
    public Boolean trySample(HttpRequest request) {
        if (request == null) {
            return null;
        }
        Object unwrapped = request.unwrap();
        if (unwrapped == null) {
            unwrapped = HttpHandler.NULL_SENTINEL;
        }
        HttpAdapter adapter = request instanceof HttpClientRequest ? new HttpClientAdapters.ToRequestAdapter((HttpClientRequest)request, unwrapped) : new HttpServerAdapters.ToRequestAdapter((HttpServerRequest)request, unwrapped);
        return this.trySample(adapter, unwrapped);
    }

    @Nullable
    public abstract <Req> Boolean trySample(HttpAdapter<Req, ?> var1, Req var2);

    static HttpSampler fromHttpRequestSampler(SamplerFunction<HttpRequest> sampler) {
        if (sampler == null) {
            throw new NullPointerException("sampler == null");
        }
        if (sampler.equals((Object)SamplerFunctions.deferDecision())) {
            return TRACE_ID;
        }
        if (sampler.equals((Object)SamplerFunctions.neverSample())) {
            return NEVER_SAMPLE;
        }
        return sampler instanceof HttpSampler ? (HttpSampler)sampler : new HttpRequestSamplerAdapter(sampler);
    }

    static SamplerFunction<HttpRequest> toHttpRequestSampler(SamplerFunction<HttpRequest> sampler) {
        if (sampler == null) {
            throw new NullPointerException("sampler == null");
        }
        if (sampler == TRACE_ID) {
            return SamplerFunctions.deferDecision();
        }
        if (sampler == NEVER_SAMPLE) {
            return SamplerFunctions.neverSample();
        }
        return sampler;
    }

    @Deprecated
    static final class FromHttpAdapter<Req>
    extends HttpRequest {
        final HttpAdapter<Req, ?> adapter;
        final Req request;
        final Span.Kind kind;

        FromHttpAdapter(HttpAdapter<Req, ?> adapter, Req request) {
            if (adapter == null) {
                throw new NullPointerException("adapter == null");
            }
            this.adapter = adapter;
            Span.Kind kind = this.kind = adapter instanceof HttpServerAdapter ? Span.Kind.SERVER : Span.Kind.CLIENT;
            if (request == null) {
                throw new NullPointerException("request == null");
            }
            this.request = request;
        }

        public Span.Kind spanKind() {
            return this.kind;
        }

        public Object unwrap() {
            return this.request;
        }

        @Override
        public String method() {
            return this.adapter.method(this.request);
        }

        @Override
        public String path() {
            return this.adapter.path(this.request);
        }

        @Override
        public String url() {
            return this.adapter.url(this.request);
        }

        @Override
        public String header(String name) {
            return this.adapter.requestHeader(this.request, name);
        }

        public String toString() {
            return this.request.toString();
        }
    }

    static final class HttpRequestSamplerAdapter
    extends HttpSampler {
        final SamplerFunction<HttpRequest> delegate;

        HttpRequestSamplerAdapter(SamplerFunction<HttpRequest> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Boolean trySample(HttpRequest request) {
            return this.delegate.trySample((Object)request);
        }

        @Override
        public <Req> Boolean trySample(HttpAdapter<Req, ?> adapter, Req request) {
            if (adapter == null) {
                throw new NullPointerException("adapter == null");
            }
            if (request == null) {
                return null;
            }
            if (request instanceof HttpRequest) {
                return this.delegate.trySample((Object)((HttpRequest)((Object)request)));
            }
            return this.delegate.trySample(new FromHttpAdapter<Req>(adapter, request));
        }

        public String toString() {
            return this.delegate.toString();
        }
    }
}

