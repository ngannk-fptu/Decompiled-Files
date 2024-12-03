/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.google.common.base.Predicate;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface MarshallerMetricsAccumulator {
    public @NonNull MarshallerMetricsCollector newMetricsCollector(MarshallerMetricsAccumulationKey var1);

    public @NonNull Collection<MarshallerMetrics> getMetricsSnapshots(Predicate<? super MarshallerMetricsAccumulationKey> var1);
}

