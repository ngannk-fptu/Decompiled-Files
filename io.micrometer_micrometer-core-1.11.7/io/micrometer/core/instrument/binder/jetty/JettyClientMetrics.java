/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.ObservationRegistry
 *  org.eclipse.jetty.client.api.ContentProvider
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Request$Listener
 *  org.eclipse.jetty.client.api.Result
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jetty.DefaultJettyClientObservationConvention;
import io.micrometer.core.instrument.binder.jetty.JettyClientContext;
import io.micrometer.core.instrument.binder.jetty.JettyClientObservationConvention;
import io.micrometer.core.instrument.binder.jetty.JettyClientTagsProvider;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.internal.OnlyOnceLoggingDenyMeterFilter;
import io.micrometer.core.instrument.observation.ObservationOrTimerCompatibleInstrumentation;
import io.micrometer.observation.ObservationRegistry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;

@Incubating(since="1.5.0")
public class JettyClientMetrics
implements Request.Listener {
    static final String DEFAULT_JETTY_CLIENT_REQUESTS_TIMER_NAME = "jetty.client.requests";
    private final MeterRegistry registry;
    private final JettyClientTagsProvider tagsProvider;
    private final String timingMetricName;
    private final String contentSizeMetricName;
    private final ObservationRegistry observationRegistry;
    @Nullable
    private final JettyClientObservationConvention convention;
    private final BiFunction<Request, Result, String> uriPatternFunction;

    @Deprecated
    protected JettyClientMetrics(MeterRegistry registry, JettyClientTagsProvider tagsProvider, String timingMetricName, String contentSizeMetricName, int maxUriTags) {
        this(registry, ObservationRegistry.NOOP, null, tagsProvider, timingMetricName, contentSizeMetricName, maxUriTags, (request, result) -> tagsProvider.uriPattern((Result)result));
    }

    private JettyClientMetrics(MeterRegistry registry, ObservationRegistry observationRegistry, @Nullable JettyClientObservationConvention convention, JettyClientTagsProvider tagsProvider, String timingMetricName, String contentSizeMetricName, int maxUriTags, BiFunction<Request, Result, String> uriPatternFunction) {
        this.registry = registry;
        this.tagsProvider = tagsProvider;
        this.timingMetricName = timingMetricName;
        this.contentSizeMetricName = contentSizeMetricName;
        this.observationRegistry = observationRegistry;
        this.convention = convention;
        this.uriPatternFunction = uriPatternFunction;
        OnlyOnceLoggingDenyMeterFilter timingMetricDenyFilter = new OnlyOnceLoggingDenyMeterFilter(() -> String.format("Reached the maximum number of URI tags for '%s'.", timingMetricName));
        OnlyOnceLoggingDenyMeterFilter contentSizeMetricDenyFilter = new OnlyOnceLoggingDenyMeterFilter(() -> String.format("Reached the maximum number of URI tags for '%s'.", contentSizeMetricName));
        registry.config().meterFilter(MeterFilter.maximumAllowableTags(this.timingMetricName, "uri", maxUriTags, timingMetricDenyFilter)).meterFilter(MeterFilter.maximumAllowableTags(this.contentSizeMetricName, "uri", maxUriTags, contentSizeMetricDenyFilter));
    }

    public void onQueued(Request request) {
        ObservationOrTimerCompatibleInstrumentation<JettyClientContext> sample = ObservationOrTimerCompatibleInstrumentation.start(this.registry, this.observationRegistry, () -> new JettyClientContext(request, this.uriPatternFunction), this.convention, DefaultJettyClientObservationConvention.INSTANCE);
        request.onComplete(result -> {
            sample.setResponse(result);
            long requestLength = Optional.ofNullable(result.getRequest().getContent()).map(ContentProvider::getLength).orElse(0L);
            Iterable<Tag> httpRequestTags = this.tagsProvider.httpRequestTags(result);
            if (requestLength >= 0L) {
                DistributionSummary.builder(this.contentSizeMetricName).description("Content sizes for Jetty HTTP client requests").tags(httpRequestTags).register(this.registry).record(requestLength);
            }
            sample.stop(this.timingMetricName, "Jetty HTTP client request timing", () -> httpRequestTags);
        });
    }

    @Deprecated
    public static Builder builder(MeterRegistry registry, JettyClientTagsProvider tagsProvider) {
        return new Builder(registry, (request, result) -> tagsProvider.uriPattern((Result)result));
    }

    public static Builder builder(MeterRegistry registry, BiFunction<Request, Result, String> uriPatternFunction) {
        return new Builder(registry, uriPatternFunction);
    }

    public static class Builder {
        private final MeterRegistry meterRegistry;
        private final BiFunction<Request, Result, String> uriPatternFunction;
        private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
        private JettyClientTagsProvider tagsProvider;
        private String timingMetricName = "jetty.client.requests";
        private String contentSizeMetricName = "jetty.client.request.size";
        private int maxUriTags = 1000;
        @Nullable
        private JettyClientObservationConvention observationConvention;

        private Builder(MeterRegistry registry, BiFunction<Request, Result, String> uriPatternFunction) {
            this.meterRegistry = registry;
            this.uriPatternFunction = uriPatternFunction;
            this.tagsProvider = result -> (String)uriPatternFunction.apply(result.getRequest(), result);
        }

        public Builder timingMetricName(String metricName) {
            this.timingMetricName = metricName;
            return this;
        }

        public Builder contentSizeMetricName(String metricName) {
            this.contentSizeMetricName = metricName;
            return this;
        }

        public Builder maxUriTags(int maxUriTags) {
            this.maxUriTags = maxUriTags;
            return this;
        }

        public Builder tagsProvider(JettyClientTagsProvider tagsProvider) {
            this.tagsProvider = tagsProvider;
            return this;
        }

        public Builder observationRegistry(ObservationRegistry observationRegistry) {
            this.observationRegistry = observationRegistry;
            return this;
        }

        public Builder observationConvention(JettyClientObservationConvention convention) {
            this.observationConvention = convention;
            return this;
        }

        public JettyClientMetrics build() {
            return new JettyClientMetrics(this.meterRegistry, this.observationRegistry, this.observationConvention, this.tagsProvider, this.timingMetricName, this.contentSizeMetricName, this.maxUriTags, this.uriPatternFunction);
        }
    }
}

