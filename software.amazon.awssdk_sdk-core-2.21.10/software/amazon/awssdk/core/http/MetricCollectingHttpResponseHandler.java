/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.SdkMetric
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.core.http;

import java.time.Duration;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.utils.Pair;

@SdkProtectedApi
public final class MetricCollectingHttpResponseHandler<T>
implements HttpResponseHandler<T> {
    public final SdkMetric<? super Duration> metric;
    public final HttpResponseHandler<T> delegateToTime;

    private MetricCollectingHttpResponseHandler(SdkMetric<? super Duration> durationMetric, HttpResponseHandler<T> delegateToTime) {
        this.metric = durationMetric;
        this.delegateToTime = delegateToTime;
    }

    public static <T> MetricCollectingHttpResponseHandler<T> create(SdkMetric<? super Duration> durationMetric, HttpResponseHandler<T> delegateToTime) {
        return new MetricCollectingHttpResponseHandler<T>(durationMetric, delegateToTime);
    }

    @Override
    public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
        Pair<Object, Duration> result = MetricUtils.measureDurationUnsafe(() -> this.delegateToTime.handle(response, executionAttributes));
        this.collector(executionAttributes).ifPresent(c -> c.reportMetric(this.metric, result.right()));
        return (T)result.left();
    }

    private Optional<MetricCollector> collector(ExecutionAttributes attributes) {
        if (attributes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.getAttribute(SdkExecutionAttribute.API_CALL_ATTEMPT_METRIC_COLLECTOR));
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return this.delegateToTime.needsConnectionLeftOpen();
    }
}

