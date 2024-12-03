/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.profiling.metrics.api.context.ContextFragment
 *  com.atlassian.profiling.metrics.api.tags.OptionalTag
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.profiling.metrics.api.context.ContextFragment;
import com.atlassian.profiling.metrics.api.tags.OptionalTag;
import com.atlassian.util.profiling.MetricTag;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

@Internal
public class MetricTagContext {
    private static final InheritableThreadLocal<Set<MetricTag.OptionalMetricTag>> threadLocal = new InheritableThreadLocal<Set<MetricTag.OptionalMetricTag>>(){

        @Override
        protected Set<MetricTag.OptionalMetricTag> childValue(Set<MetricTag.OptionalMetricTag> parentValue) {
            return parentValue == null ? null : new HashSet<MetricTag.OptionalMetricTag>(parentValue);
        }
    };

    @Nonnull
    public static ContextFragment put(OptionalTag ... tags) {
        Objects.requireNonNull(tags, "tags");
        List closeableList = Arrays.stream(tags).map(tag -> MetricTag.optionalOf(tag.getKey(), tag.getValue())).map(MetricTagContext::put).collect(Collectors.toList());
        return () -> closeableList.forEach(ContextFragment::close);
    }

    public static Closeable put(MetricTag.OptionalMetricTag tag) {
        Set<MetricTag.OptionalMetricTag> set = MetricTagContext.getOrCreateLocalSet();
        if (set.add(tag)) {
            return () -> set.remove(tag);
        }
        return () -> {};
    }

    @Nonnull
    public static Set<MetricTag.OptionalMetricTag> getAll() {
        Set set = (Set)threadLocal.get();
        return set == null ? Collections.emptySet() : new HashSet(set);
    }

    private static Set<MetricTag.OptionalMetricTag> getOrCreateLocalSet() {
        HashSet set = (HashSet)threadLocal.get();
        if (set == null) {
            set = new HashSet();
            threadLocal.set(set);
        }
        return set;
    }

    public static interface Closeable
    extends ContextFragment {
    }
}

