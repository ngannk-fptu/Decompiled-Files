/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  com.atlassian.profiling.metrics.api.context.ContextFragment
 *  com.atlassian.profiling.metrics.api.context.MetricContext
 *  com.atlassian.profiling.metrics.api.tags.OptionalTag
 *  com.atlassian.util.profiling.MetricTagContext
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.profiling.metrics.context;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import com.atlassian.profiling.metrics.api.context.ContextFragment;
import com.atlassian.profiling.metrics.api.context.MetricContext;
import com.atlassian.profiling.metrics.api.tags.OptionalTag;
import com.atlassian.util.profiling.MetricTagContext;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Internal
public class MetricContextAdaptor
implements MetricContext {
    public ContextFragment put(OptionalTag ... tags) {
        Objects.requireNonNull(tags, "tags");
        return MetricTagContext.put((OptionalTag[])tags);
    }

    public Set<OptionalTag> getAll() {
        return MetricTagContext.getAll().stream().map(tag -> tag).collect(Collectors.toSet());
    }
}

