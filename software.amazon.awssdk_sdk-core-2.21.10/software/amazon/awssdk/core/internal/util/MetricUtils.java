/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.HttpMetric
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.NoOpMetricCollector
 *  software.amazon.awssdk.metrics.SdkMetric
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.core.internal.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.NoOpMetricCollector;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public final class MetricUtils {
    private MetricUtils() {
    }

    public static <T> Pair<T, Duration> measureDuration(Supplier<T> c) {
        long start = System.nanoTime();
        T result = c.get();
        Duration d = Duration.ofNanos(System.nanoTime() - start);
        return Pair.of(result, (Object)d);
    }

    public static <T> CompletableFuture<T> reportDuration(Supplier<CompletableFuture<T>> c, MetricCollector metricCollector, SdkMetric<Duration> metric) {
        long start = System.nanoTime();
        CompletableFuture<T> result = c.get();
        result.whenComplete((r, t) -> {
            Duration d = Duration.ofNanos(System.nanoTime() - start);
            metricCollector.reportMetric(metric, (Object)d);
        });
        return result;
    }

    public static <T> Pair<T, Duration> measureDurationUnsafe(Callable<T> c) throws Exception {
        long start = System.nanoTime();
        T result = c.call();
        Duration d = Duration.ofNanos(System.nanoTime() - start);
        return Pair.of(result, (Object)d);
    }

    public static void collectServiceEndpointMetrics(MetricCollector metricCollector, SdkHttpFullRequest httpRequest) {
        if (metricCollector != null && !(metricCollector instanceof NoOpMetricCollector) && httpRequest != null) {
            URI requestUri = httpRequest.getUri();
            try {
                URI serviceEndpoint = new URI(requestUri.getScheme(), requestUri.getAuthority(), null, null, null);
                metricCollector.reportMetric(CoreMetric.SERVICE_ENDPOINT, (Object)serviceEndpoint);
            }
            catch (URISyntaxException e) {
                throw SdkClientException.create("Unable to collect SERVICE_ENDPOINT metric", e);
            }
        }
    }

    public static void collectHttpMetrics(MetricCollector metricCollector, SdkHttpFullResponse httpResponse) {
        if (metricCollector != null && !(metricCollector instanceof NoOpMetricCollector) && httpResponse != null) {
            metricCollector.reportMetric(HttpMetric.HTTP_STATUS_CODE, (Object)httpResponse.statusCode());
            HttpResponseHandler.X_AMZN_REQUEST_ID_HEADERS.forEach(h -> httpResponse.firstMatchingHeader(h).ifPresent(v -> metricCollector.reportMetric(CoreMetric.AWS_REQUEST_ID, v)));
            httpResponse.firstMatchingHeader("x-amz-id-2").ifPresent(v -> metricCollector.reportMetric(CoreMetric.AWS_EXTENDED_REQUEST_ID, v));
        }
    }

    public static MetricCollector createAttemptMetricsCollector(RequestExecutionContext context) {
        MetricCollector parentCollector = context.executionContext().metricCollector();
        if (parentCollector != null) {
            return parentCollector.createChild("ApiCallAttempt");
        }
        return NoOpMetricCollector.create();
    }

    public static MetricCollector createHttpMetricsCollector(RequestExecutionContext context) {
        MetricCollector parentCollector = context.attemptMetricCollector();
        if (parentCollector != null) {
            return parentCollector.createChild("HttpClient");
        }
        return NoOpMetricCollector.create();
    }
}

