/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline;

import com.atlassian.analytics.client.pipeline.AnalyticsPipeline;
import java.util.List;

public class SwitchingAnalyticsPipeline
implements AnalyticsPipeline {
    private final List<AnalyticsPipeline> pipelines;

    public SwitchingAnalyticsPipeline(List<AnalyticsPipeline> pipelines) {
        this.pipelines = pipelines;
    }

    @Override
    public void process(Object event) {
        this.pipelines.stream().filter(pipeline -> pipeline.canHandle(event)).findFirst().orElseThrow(() -> new IllegalArgumentException("Analytic event should be consumable by at least one pipeline")).process(event);
    }

    @Override
    public boolean canHandle(Object event) {
        return this.pipelines.stream().reduce(false, (a, b) -> a != false || b.canHandle(event), (a, b) -> a != false || b != false);
    }
}

