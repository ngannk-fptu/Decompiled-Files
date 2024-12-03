/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics.internal;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.metrics.MetricCategory;
import software.amazon.awssdk.metrics.MetricLevel;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultSdkMetric<T>
extends AttributeMap.Key<T>
implements SdkMetric<T> {
    private static final ConcurrentHashMap<SdkMetric<?>, Boolean> SDK_METRICS = new ConcurrentHashMap();
    private final String name;
    private final Class<T> clzz;
    private final Set<MetricCategory> categories;
    private final MetricLevel level;

    private DefaultSdkMetric(String name, Class<T> clzz, MetricLevel level, Set<MetricCategory> categories) {
        super(clzz);
        this.name = Validate.notBlank(name, "name must not be blank", new Object[0]);
        this.clzz = Validate.notNull(clzz, "clzz must not be null", new Object[0]);
        this.level = Validate.notNull(level, "level must not be null", new Object[0]);
        Validate.notEmpty(categories, "categories must not be empty", new Object[0]);
        this.categories = EnumSet.copyOf(categories);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Set<MetricCategory> categories() {
        return Collections.unmodifiableSet(this.categories);
    }

    @Override
    public MetricLevel level() {
        return this.level;
    }

    @Override
    public Class<T> valueClass() {
        return this.clzz;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultSdkMetric that = (DefaultSdkMetric)o;
        return this.name.equals(that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return ToString.builder("DefaultMetric").add("name", this.name).add("categories", this.categories()).build();
    }

    public static <T> SdkMetric<T> create(String name, Class<T> clzz, MetricLevel level, MetricCategory c1, MetricCategory ... cn) {
        Stream<MetricCategory> categoryStream = Stream.of(c1);
        if (cn != null) {
            categoryStream = Stream.concat(categoryStream, Stream.of(cn));
        }
        Set<MetricCategory> categories = categoryStream.collect(Collectors.toSet());
        return DefaultSdkMetric.create(name, clzz, level, categories);
    }

    public static <T> SdkMetric<T> create(String name, Class<T> clzz, MetricLevel level, Set<MetricCategory> categories) {
        Validate.noNullElements(categories, "categories must not contain null elements", new Object[0]);
        DefaultSdkMetric<T> event = new DefaultSdkMetric<T>(name, clzz, level, categories);
        if (SDK_METRICS.putIfAbsent(event, Boolean.TRUE) != null) {
            throw new IllegalArgumentException("Metric with name " + name + " has already been created");
        }
        return event;
    }

    @SdkTestInternalApi
    static void clearDeclaredMetrics() {
        SDK_METRICS.clear();
    }

    @SdkTestInternalApi
    static Set<SdkMetric<?>> declaredEvents() {
        return SDK_METRICS.keySet();
    }
}

