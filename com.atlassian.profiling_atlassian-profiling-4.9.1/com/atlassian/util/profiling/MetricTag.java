/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.profiling.metrics.api.tags.OptionalTag
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.profiling.metrics.api.tags.OptionalTag;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

@ParametersAreNonnullByDefault
@Internal
@Immutable
public abstract class MetricTag {
    public static final RequiredMetricTag SEND_ANALYTICS = new RequiredMetricTag("atl-analytics", "true");
    public static final String FROM_PLUGIN_KEY_TAG_KEY = "fromPluginKey";
    public static final String INVOKER_PLUGIN_KEY_TAG_KEY = "invokerPluginKey";
    public static final String SUBCATEGORY = "subCategory";
    @VisibleForTesting
    public static final String UNDEFINED_TAG_VALUE = "undefined";
    private final String key;
    private final String value;

    private MetricTag(String key, @Nullable String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.isNull(value) ? UNDEFINED_TAG_VALUE : value;
    }

    @Nonnull
    public String getKey() {
        return this.key;
    }

    @Nonnull
    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.key + "=" + this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricTag)) {
            return false;
        }
        MetricTag metricTag = (MetricTag)o;
        return this.getClass() == o.getClass() && this.key.equals(metricTag.key) && this.value.equals(metricTag.value);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    @Nonnull
    public static RequiredMetricTag of(String key, @Nullable String value) {
        return new RequiredMetricTag(key, value);
    }

    @Nonnull
    public static RequiredMetricTag of(String key, int value) {
        return new RequiredMetricTag(key, String.valueOf(value));
    }

    @Nonnull
    public static RequiredMetricTag of(String key, boolean value) {
        return new RequiredMetricTag(key, String.valueOf(value));
    }

    @Nonnull
    public static OptionalMetricTag optionalOf(String key, @Nullable String value) {
        return new OptionalMetricTag(key, value);
    }

    @Nonnull
    public static OptionalMetricTag optionalOf(String key, int value) {
        return new OptionalMetricTag(key, String.valueOf(value));
    }

    @Nonnull
    public static OptionalMetricTag optionalOf(String key, boolean value) {
        return new OptionalMetricTag(key, String.valueOf(value));
    }

    public static final class OptionalMetricTag
    extends MetricTag
    implements OptionalTag {
        private OptionalMetricTag(String key, @Nullable String value) {
            super(key, value);
        }

        RequiredMetricTag convert() {
            return new RequiredMetricTag(this.getKey(), this.getValue());
        }
    }

    public static final class RequiredMetricTag
    extends MetricTag {
        private RequiredMetricTag(String key, @Nullable String value) {
            super(key, value);
        }
    }
}

