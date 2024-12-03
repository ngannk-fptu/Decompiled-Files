/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  com.atlassian.profiling.metrics.api.tags.OptionalTag
 *  com.atlassian.profiling.metrics.api.tags.TagFactory
 *  com.atlassian.util.profiling.MetricTag
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.profiling.metrics.tags;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import com.atlassian.profiling.metrics.api.tags.OptionalTag;
import com.atlassian.profiling.metrics.api.tags.TagFactory;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Internal
public class PrefixedTagFactoryAdaptor
implements TagFactory {
    private final String keyPrefix;

    @VisibleForTesting
    public PrefixedTagFactoryAdaptor(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public OptionalTag createOptionalTag(String key, String value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        return MetricTag.optionalOf((String)(this.keyPrefix + key), (String)value);
    }
}

