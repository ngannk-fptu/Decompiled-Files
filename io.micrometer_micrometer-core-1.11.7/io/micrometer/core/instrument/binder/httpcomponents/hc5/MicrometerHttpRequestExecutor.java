/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.ObservationRegistry
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.impl.io.HttpRequestExecutor
 *  org.apache.hc.core5.http.io.HttpClientConnection
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Timeout
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ApacheHttpClientContext;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ApacheHttpClientObservationConvention;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.DefaultApacheHttpClientObservationConvention;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.DefaultUriMapper;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.HttpContextUtils;
import io.micrometer.core.instrument.observation.ObservationOrTimerCompatibleInstrumentation;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;

public class MicrometerHttpRequestExecutor
extends HttpRequestExecutor {
    static final String METER_NAME = "httpcomponents.httpclient.request";
    private final MeterRegistry registry;
    private final ObservationRegistry observationRegistry;
    @Nullable
    private final ApacheHttpClientObservationConvention convention;
    private final Function<HttpRequest, String> uriMapper;
    private final Iterable<Tag> extraTags;
    private final boolean exportTagsForRoute;

    private MicrometerHttpRequestExecutor(Timeout waitForContinue, MeterRegistry registry, Function<HttpRequest, String> uriMapper, Iterable<Tag> extraTags, boolean exportTagsForRoute, ObservationRegistry observationRegistry, @Nullable ApacheHttpClientObservationConvention convention) {
        super(waitForContinue, null, null);
        this.registry = Optional.ofNullable(registry).orElseThrow(() -> new IllegalArgumentException("registry is required but has been initialized with null"));
        this.uriMapper = Optional.ofNullable(uriMapper).orElseThrow(() -> new IllegalArgumentException("uriMapper is required but has been initialized with null"));
        this.extraTags = Optional.ofNullable(extraTags).orElse(Collections.emptyList());
        this.exportTagsForRoute = exportTagsForRoute;
        this.observationRegistry = observationRegistry;
        this.convention = convention;
    }

    public static Builder builder(MeterRegistry registry) {
        return new Builder(registry);
    }

    public ClassicHttpResponse execute(ClassicHttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
        ObservationOrTimerCompatibleInstrumentation<ApacheHttpClientContext> sample = ObservationOrTimerCompatibleInstrumentation.start(this.registry, this.observationRegistry, () -> new ApacheHttpClientContext((HttpRequest)request, context, this.uriMapper, this.exportTagsForRoute), this.convention, DefaultApacheHttpClientObservationConvention.INSTANCE);
        String statusCodeOrError = "UNKNOWN";
        Outcome statusOutcome = Outcome.UNKNOWN;
        try {
            ClassicHttpResponse response = super.execute(request, conn, context);
            sample.setResponse(response);
            statusCodeOrError = DefaultApacheHttpClientObservationConvention.INSTANCE.getStatusValue((HttpResponse)response, null);
            statusOutcome = DefaultApacheHttpClientObservationConvention.INSTANCE.getStatusOutcome((HttpResponse)response);
            ClassicHttpResponse classicHttpResponse = response;
            return classicHttpResponse;
        }
        catch (IOException | RuntimeException | HttpException e) {
            statusCodeOrError = "IO_ERROR";
            sample.setThrowable(e);
            throw e;
        }
        finally {
            String status = statusCodeOrError;
            String outcome = statusOutcome.name();
            sample.stop(METER_NAME, "Duration of Apache HttpClient request execution", () -> Tags.of("method", DefaultApacheHttpClientObservationConvention.INSTANCE.getMethodString((HttpRequest)request), "uri", this.uriMapper.apply((HttpRequest)request), "status", status, "outcome", outcome).and(this.exportTagsForRoute ? HttpContextUtils.generateTagsForRoute(context) : Tags.empty()).and(this.extraTags));
        }
    }

    public static class Builder {
        private final MeterRegistry registry;
        private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
        private Timeout waitForContinue = HttpRequestExecutor.DEFAULT_WAIT_FOR_CONTINUE;
        private Iterable<Tag> extraTags = Collections.emptyList();
        private Function<HttpRequest, String> uriMapper = new DefaultUriMapper();
        private boolean exportTagsForRoute = false;
        @Nullable
        private ApacheHttpClientObservationConvention observationConvention;

        Builder(MeterRegistry registry) {
            this.registry = registry;
        }

        public Builder waitForContinue(Timeout waitForContinue) {
            this.waitForContinue = waitForContinue;
            return this;
        }

        public Builder tags(Iterable<Tag> tags) {
            this.extraTags = tags;
            return this;
        }

        public Builder uriMapper(Function<HttpRequest, String> uriMapper) {
            this.uriMapper = uriMapper;
            return this;
        }

        public Builder exportTagsForRoute(boolean exportTagsForRoute) {
            this.exportTagsForRoute = exportTagsForRoute;
            return this;
        }

        public Builder observationRegistry(ObservationRegistry observationRegistry) {
            this.observationRegistry = observationRegistry;
            return this;
        }

        public Builder observationConvention(ApacheHttpClientObservationConvention convention) {
            this.observationConvention = convention;
            return this;
        }

        public MicrometerHttpRequestExecutor build() {
            return new MicrometerHttpRequestExecutor(this.waitForContinue, this.registry, this.uriMapper, this.extraTags, this.exportTagsForRoute, this.observationRegistry, this.observationConvention);
        }
    }
}

