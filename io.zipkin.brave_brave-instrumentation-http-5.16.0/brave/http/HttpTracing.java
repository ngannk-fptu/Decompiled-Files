/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tracing
 *  brave.internal.Nullable
 *  brave.propagation.Propagation
 *  brave.sampler.SamplerFunction
 *  brave.sampler.SamplerFunctions
 */
package brave.http;

import brave.Tracing;
import brave.http.HttpClientParser;
import brave.http.HttpClientParserAdapter;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.http.HttpRequestParserAdapters;
import brave.http.HttpResponseParser;
import brave.http.HttpResponseParserAdapters;
import brave.http.HttpSampler;
import brave.http.HttpServerParser;
import brave.http.HttpServerParserAdapter;
import brave.internal.Nullable;
import brave.propagation.Propagation;
import brave.sampler.SamplerFunction;
import brave.sampler.SamplerFunctions;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;

public class HttpTracing
implements Closeable {
    static final AtomicReference<HttpTracing> CURRENT = new AtomicReference();
    final Tracing tracing;
    final HttpRequestParser clientRequestParser;
    final HttpRequestParser serverRequestParser;
    final HttpResponseParser clientResponseParser;
    final HttpResponseParser serverResponseParser;
    final SamplerFunction<HttpRequest> clientSampler;
    final SamplerFunction<HttpRequest> serverSampler;
    final Propagation<String> propagation;
    final String serverName;

    public static HttpTracing create(Tracing tracing) {
        return HttpTracing.newBuilder(tracing).build();
    }

    public static Builder newBuilder(Tracing tracing) {
        return new Builder(tracing);
    }

    public Tracing tracing() {
        return this.tracing;
    }

    public HttpRequestParser clientRequestParser() {
        return this.clientRequestParser;
    }

    public HttpResponseParser clientResponseParser() {
        return this.clientResponseParser;
    }

    @Deprecated
    public HttpClientParser clientParser() {
        if (this.clientRequestParser instanceof HttpRequestParserAdapters.ClientAdapter) {
            return (HttpClientParser)((HttpRequestParserAdapters.ClientAdapter)this.clientRequestParser).parser;
        }
        return new HttpClientParserAdapter(this.clientRequestParser, this.clientResponseParser, this.tracing.currentTraceContext(), this.tracing.errorParser());
    }

    public String serverName() {
        return this.serverName;
    }

    public HttpTracing clientOf(String serverName) {
        return this.toBuilder().serverName(serverName).build();
    }

    public HttpRequestParser serverRequestParser() {
        return this.serverRequestParser;
    }

    public HttpResponseParser serverResponseParser() {
        return this.serverResponseParser;
    }

    @Deprecated
    public HttpServerParser serverParser() {
        if (this.serverRequestParser instanceof HttpRequestParserAdapters.ServerAdapter) {
            return (HttpServerParser)((HttpRequestParserAdapters.ServerAdapter)this.serverRequestParser).parser;
        }
        return new HttpServerParserAdapter(this.serverRequestParser, this.serverResponseParser, this.tracing.currentTraceContext(), this.tracing.errorParser());
    }

    @Deprecated
    public HttpSampler clientSampler() {
        return HttpSampler.fromHttpRequestSampler(this.clientSampler);
    }

    public SamplerFunction<HttpRequest> clientRequestSampler() {
        return this.clientSampler;
    }

    @Deprecated
    public HttpSampler serverSampler() {
        return HttpSampler.fromHttpRequestSampler(this.serverSampler);
    }

    public SamplerFunction<HttpRequest> serverRequestSampler() {
        return this.serverSampler;
    }

    public Propagation<String> propagation() {
        return this.propagation;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    HttpTracing(Builder builder) {
        this.tracing = builder.tracing;
        this.clientRequestParser = builder.clientRequestParser;
        this.serverRequestParser = builder.serverRequestParser;
        this.clientResponseParser = builder.clientResponseParser;
        this.serverResponseParser = builder.serverResponseParser;
        this.clientSampler = builder.clientSampler;
        this.serverSampler = builder.serverSampler;
        this.propagation = builder.propagation;
        this.serverName = builder.serverName;
        CURRENT.compareAndSet(null, this);
    }

    @Nullable
    public static HttpTracing current() {
        return CURRENT.get();
    }

    @Override
    public void close() {
        CURRENT.compareAndSet(this, null);
    }

    public static final class Builder {
        Tracing tracing;
        HttpRequestParser clientRequestParser;
        HttpRequestParser serverRequestParser;
        HttpResponseParser clientResponseParser;
        HttpResponseParser serverResponseParser;
        SamplerFunction<HttpRequest> clientSampler;
        SamplerFunction<HttpRequest> serverSampler;
        Propagation<String> propagation;
        String serverName;

        Builder(Tracing tracing) {
            if (tracing == null) {
                throw new NullPointerException("tracing == null");
            }
            this.tracing = tracing;
            this.clientRequestParser = this.serverRequestParser = HttpRequestParser.DEFAULT;
            this.clientResponseParser = this.serverResponseParser = HttpResponseParser.DEFAULT;
            this.clientSampler = this.serverSampler = SamplerFunctions.deferDecision();
            this.propagation = tracing.propagation();
            this.serverName = "";
        }

        Builder(HttpTracing source) {
            this.tracing = source.tracing;
            this.clientRequestParser = source.clientRequestParser;
            this.serverRequestParser = source.serverRequestParser;
            this.clientResponseParser = source.clientResponseParser;
            this.serverResponseParser = source.serverResponseParser;
            this.clientSampler = source.clientSampler;
            this.serverSampler = source.serverSampler;
            this.propagation = source.propagation;
            this.serverName = source.serverName;
        }

        public Builder tracing(Tracing tracing) {
            if (tracing == null) {
                throw new NullPointerException("tracing == null");
            }
            this.tracing = tracing;
            return this;
        }

        public Builder clientRequestParser(HttpRequestParser clientRequestParser) {
            if (clientRequestParser == null) {
                throw new NullPointerException("clientRequestParser == null");
            }
            this.clientRequestParser = clientRequestParser;
            return this;
        }

        public Builder clientResponseParser(HttpResponseParser clientResponseParser) {
            if (clientResponseParser == null) {
                throw new NullPointerException("clientResponseParser == null");
            }
            this.clientResponseParser = clientResponseParser;
            return this;
        }

        @Deprecated
        public Builder clientParser(HttpClientParser clientParser) {
            if (clientParser == null) {
                throw new NullPointerException("clientParser == null");
            }
            this.clientRequestParser = new HttpRequestParserAdapters.ClientAdapter(this.tracing.currentTraceContext(), clientParser);
            this.clientResponseParser = new HttpResponseParserAdapters.ClientAdapter(this.tracing.currentTraceContext(), clientParser);
            this.tracing.errorParser();
            return this;
        }

        Builder serverName(String serverName) {
            if (serverName == null) {
                throw new NullPointerException("serverName == null");
            }
            this.serverName = serverName;
            return this;
        }

        public Builder serverRequestParser(HttpRequestParser serverRequestParser) {
            if (serverRequestParser == null) {
                throw new NullPointerException("serverRequestParser == null");
            }
            this.serverRequestParser = serverRequestParser;
            return this;
        }

        public Builder serverResponseParser(HttpResponseParser serverResponseParser) {
            if (serverResponseParser == null) {
                throw new NullPointerException("serverResponseParser == null");
            }
            this.serverResponseParser = serverResponseParser;
            return this;
        }

        @Deprecated
        public Builder serverParser(HttpServerParser serverParser) {
            if (serverParser == null) {
                throw new NullPointerException("serverParser == null");
            }
            this.serverRequestParser = new HttpRequestParserAdapters.ServerAdapter(this.tracing.currentTraceContext(), serverParser);
            this.serverResponseParser = new HttpResponseParserAdapters.ServerAdapter(this.tracing.currentTraceContext(), serverParser);
            return this;
        }

        public Builder clientSampler(HttpSampler clientSampler) {
            if (clientSampler == null) {
                throw new NullPointerException("clientSampler == null");
            }
            return this.clientSampler((SamplerFunction<HttpRequest>)clientSampler);
        }

        public Builder clientSampler(SamplerFunction<HttpRequest> clientSampler) {
            if (clientSampler == null) {
                throw new NullPointerException("clientSampler == null");
            }
            this.clientSampler = HttpSampler.toHttpRequestSampler(clientSampler);
            return this;
        }

        public Builder serverSampler(HttpSampler serverSampler) {
            return this.serverSampler((SamplerFunction<HttpRequest>)serverSampler);
        }

        public Builder serverSampler(SamplerFunction<HttpRequest> serverSampler) {
            if (serverSampler == null) {
                throw new NullPointerException("serverSampler == null");
            }
            this.serverSampler = HttpSampler.toHttpRequestSampler(serverSampler);
            return this;
        }

        public Builder propagation(Propagation<String> propagation) {
            if (propagation == null) {
                throw new NullPointerException("propagation == null");
            }
            this.propagation = propagation;
            return this;
        }

        public HttpTracing build() {
            return new HttpTracing(this);
        }
    }
}

