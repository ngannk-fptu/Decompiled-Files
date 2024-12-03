/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.profiling.metrics.api.context;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.profiling.metrics.api.context.ContextFragment;
import com.atlassian.profiling.metrics.api.tags.OptionalTag;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface MetricContext {
    public ContextFragment put(OptionalTag ... var1);

    public Set<OptionalTag> getAll();
}

