/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCategory;
import software.amazon.awssdk.metrics.MetricLevel;
import software.amazon.awssdk.metrics.SdkMetric;

@SdkPublicApi
public final class Http2Metric {
    public static final SdkMetric<Integer> LOCAL_STREAM_WINDOW_SIZE_IN_BYTES = Http2Metric.metric("LocalStreamWindowSize", Integer.class, MetricLevel.TRACE);
    public static final SdkMetric<Integer> REMOTE_STREAM_WINDOW_SIZE_IN_BYTES = Http2Metric.metric("RemoteStreamWindowSize", Integer.class, MetricLevel.TRACE);

    private Http2Metric() {
    }

    private static <T> SdkMetric<T> metric(String name, Class<T> clzz, MetricLevel level) {
        return SdkMetric.create(name, clzz, level, MetricCategory.CORE, MetricCategory.HTTP_CLIENT);
    }
}

