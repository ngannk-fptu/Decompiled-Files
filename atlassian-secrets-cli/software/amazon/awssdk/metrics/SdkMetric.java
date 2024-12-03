/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics;

import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCategory;
import software.amazon.awssdk.metrics.MetricLevel;
import software.amazon.awssdk.metrics.internal.DefaultSdkMetric;

@SdkPublicApi
public interface SdkMetric<T> {
    public String name();

    public Set<MetricCategory> categories();

    public MetricLevel level();

    public Class<T> valueClass();

    public static <T> SdkMetric<T> create(String name, Class<T> clzz, MetricLevel level, MetricCategory c1, MetricCategory ... cn) {
        return DefaultSdkMetric.create(name, clzz, level, c1, cn);
    }

    public static <T> SdkMetric<T> create(String name, Class<T> clzz, MetricLevel level, Set<MetricCategory> categories) {
        return DefaultSdkMetric.create(name, clzz, level, categories);
    }
}

