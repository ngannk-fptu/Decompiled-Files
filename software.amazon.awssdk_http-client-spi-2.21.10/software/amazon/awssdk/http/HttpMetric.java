/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.metrics.MetricCategory
 *  software.amazon.awssdk.metrics.MetricLevel
 *  software.amazon.awssdk.metrics.SdkMetric
 */
package software.amazon.awssdk.http;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCategory;
import software.amazon.awssdk.metrics.MetricLevel;
import software.amazon.awssdk.metrics.SdkMetric;

@SdkPublicApi
public final class HttpMetric {
    public static final SdkMetric<String> HTTP_CLIENT_NAME = HttpMetric.metric("HttpClientName", String.class, MetricLevel.INFO);
    public static final SdkMetric<Integer> MAX_CONCURRENCY = HttpMetric.metric("MaxConcurrency", Integer.class, MetricLevel.INFO);
    public static final SdkMetric<Integer> AVAILABLE_CONCURRENCY = HttpMetric.metric("AvailableConcurrency", Integer.class, MetricLevel.INFO);
    public static final SdkMetric<Integer> LEASED_CONCURRENCY = HttpMetric.metric("LeasedConcurrency", Integer.class, MetricLevel.INFO);
    public static final SdkMetric<Integer> PENDING_CONCURRENCY_ACQUIRES = HttpMetric.metric("PendingConcurrencyAcquires", Integer.class, MetricLevel.INFO);
    public static final SdkMetric<Integer> HTTP_STATUS_CODE = HttpMetric.metric("HttpStatusCode", Integer.class, MetricLevel.TRACE);
    public static final SdkMetric<Duration> CONCURRENCY_ACQUIRE_DURATION = HttpMetric.metric("ConcurrencyAcquireDuration", Duration.class, MetricLevel.INFO);

    private HttpMetric() {
    }

    private static <T> SdkMetric<T> metric(String name, Class<T> clzz, MetricLevel level) {
        return SdkMetric.create((String)name, clzz, (MetricLevel)level, (MetricCategory)MetricCategory.CORE, (MetricCategory[])new MetricCategory[]{MetricCategory.HTTP_CLIENT});
    }
}

