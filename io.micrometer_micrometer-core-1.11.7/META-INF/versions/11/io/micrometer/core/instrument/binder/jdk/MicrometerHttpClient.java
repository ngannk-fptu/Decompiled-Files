/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.ObservationRegistry
 */
package io.micrometer.core.instrument.binder.jdk;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.jdk.DefaultHttpClientObservationConvention;
import io.micrometer.core.instrument.binder.jdk.HttpClientContext;
import io.micrometer.core.instrument.binder.jdk.HttpClientObservationConvention;
import io.micrometer.core.instrument.binder.jdk.HttpClientObservationDocumentation;
import io.micrometer.core.instrument.observation.ObservationOrTimerCompatibleInstrumentation;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

public class MicrometerHttpClient
extends HttpClient {
    public static final String URI_PATTERN_HEADER = "URI_PATTERN";
    private final MeterRegistry meterRegistry;
    private final HttpClient client;
    @Nullable
    private final ObservationRegistry observationRegistry;
    @Nullable
    private final HttpClientObservationConvention customObservationConvention;
    private final Function<HttpRequest, String> uriMapper;

    private MicrometerHttpClient(MeterRegistry meterRegistry, HttpClient client, @Nullable ObservationRegistry observationRegistry, @Nullable HttpClientObservationConvention customObservationConvention, Function<HttpRequest, String> uriMapper) {
        this.meterRegistry = meterRegistry;
        this.client = client;
        this.observationRegistry = observationRegistry;
        this.customObservationConvention = customObservationConvention;
        this.uriMapper = uriMapper;
    }

    public static InstrumentationBuilder instrumentationBuilder(HttpClient httpClient, MeterRegistry meterRegistry) {
        return new InstrumentationBuilder(httpClient, meterRegistry);
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        return this.client.cookieHandler();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return this.client.connectTimeout();
    }

    @Override
    public HttpClient.Redirect followRedirects() {
        return this.client.followRedirects();
    }

    @Override
    public Optional<ProxySelector> proxy() {
        return this.client.proxy();
    }

    @Override
    public SSLContext sslContext() {
        return this.client.sslContext();
    }

    @Override
    public SSLParameters sslParameters() {
        return this.client.sslParameters();
    }

    @Override
    public Optional<Authenticator> authenticator() {
        return this.client.authenticator();
    }

    @Override
    public HttpClient.Version version() {
        return this.client.version();
    }

    @Override
    public Optional<Executor> executor() {
        return this.client.executor();
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        HttpRequest.Builder httpRequestBuilder = this.decorate(httpRequest);
        ObservationOrTimerCompatibleInstrumentation<HttpClientContext> instrumentation = this.observationOrTimer(httpRequestBuilder);
        HttpRequest request = httpRequestBuilder.build();
        HttpResponse<T> response = null;
        try {
            response = this.client.send(request, bodyHandler);
            instrumentation.setResponse(response);
            HttpResponse<T> httpResponse = response;
            return httpResponse;
        }
        catch (IOException ex) {
            instrumentation.setThrowable(ex);
            throw ex;
        }
        finally {
            this.stopObservationOrTimer(instrumentation, request, response);
        }
    }

    private <T> void stopObservationOrTimer(ObservationOrTimerCompatibleInstrumentation<HttpClientContext> instrumentation, HttpRequest request, @Nullable HttpResponse<T> res) {
        instrumentation.stop(DefaultHttpClientObservationConvention.INSTANCE.getName(), "Timer for JDK's HttpClient", () -> {
            Tags tags = Tags.of(HttpClientObservationDocumentation.LowCardinalityKeys.METHOD.asString(), request.method(), HttpClientObservationDocumentation.LowCardinalityKeys.URI.asString(), DefaultHttpClientObservationConvention.INSTANCE.getUriTag(request, res, this.uriMapper));
            if (res != null) {
                tags = tags.and(Tag.of(HttpClientObservationDocumentation.LowCardinalityKeys.STATUS.asString(), String.valueOf(res.statusCode()))).and(Tag.of(HttpClientObservationDocumentation.LowCardinalityKeys.OUTCOME.asString(), Outcome.forStatus(res.statusCode()).name()));
            }
            return tags;
        });
    }

    private ObservationOrTimerCompatibleInstrumentation<HttpClientContext> observationOrTimer(HttpRequest.Builder httpRequestBuilder) {
        return ObservationOrTimerCompatibleInstrumentation.start(this.meterRegistry, this.observationRegistry, () -> {
            HttpClientContext context = new HttpClientContext(this.uriMapper);
            context.setCarrier(httpRequestBuilder);
            return context;
        }, this.customObservationConvention, DefaultHttpClientObservationConvention.INSTANCE);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler) {
        return this.sendAsync(httpRequest, bodyHandler, null);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler, @Nullable HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        HttpRequest.Builder httpRequestBuilder = this.decorate(httpRequest);
        ObservationOrTimerCompatibleInstrumentation<HttpClientContext> instrumentation = this.observationOrTimer(httpRequestBuilder);
        HttpRequest request = httpRequestBuilder.build();
        return this.client.sendAsync(request, bodyHandler, pushPromiseHandler).handle((response, throwable) -> {
            if (throwable != null) {
                instrumentation.setThrowable((Throwable)throwable);
            }
            instrumentation.setResponse(response);
            this.stopObservationOrTimer(instrumentation, request, (HttpResponse)response);
            return response;
        });
    }

    private HttpRequest.Builder decorate(HttpRequest httpRequest) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(httpRequest.uri());
        builder.expectContinue(httpRequest.expectContinue());
        httpRequest.headers().map().forEach((key, values) -> values.forEach(value -> builder.header((String)key, (String)value)));
        httpRequest.bodyPublisher().ifPresentOrElse(publisher -> builder.method(httpRequest.method(), (HttpRequest.BodyPublisher)publisher), () -> {
            switch (httpRequest.method()) {
                case "GET": {
                    builder.GET();
                    break;
                }
                case "DELETE": {
                    builder.DELETE();
                    break;
                }
                default: {
                    throw new IllegalStateException(httpRequest.method());
                }
            }
        });
        httpRequest.timeout().ifPresent(builder::timeout);
        httpRequest.version().ifPresent(builder::version);
        return builder;
    }

    public static class InstrumentationBuilder {
        private final HttpClient client;
        private final MeterRegistry meterRegistry;
        @Nullable
        private ObservationRegistry observationRegistry;
        @Nullable
        private HttpClientObservationConvention customObservationConvention;
        private Function<HttpRequest, String> uriMapper = request -> request.headers().firstValue(MicrometerHttpClient.URI_PATTERN_HEADER).orElse("UNKNOWN");

        public InstrumentationBuilder(HttpClient client, MeterRegistry meterRegistry) {
            this.client = client;
            this.meterRegistry = meterRegistry;
        }

        public InstrumentationBuilder observationRegistry(ObservationRegistry observationRegistry) {
            this.observationRegistry = observationRegistry;
            return this;
        }

        public InstrumentationBuilder customObservationConvention(HttpClientObservationConvention customObservationConvention) {
            this.customObservationConvention = customObservationConvention;
            return this;
        }

        public InstrumentationBuilder uriMapper(Function<HttpRequest, String> uriMapper) {
            this.uriMapper = uriMapper;
            return this;
        }

        public HttpClient build() {
            return new MicrometerHttpClient(this.meterRegistry, this.client, this.observationRegistry, this.customObservationConvention, this.uriMapper);
        }
    }
}

