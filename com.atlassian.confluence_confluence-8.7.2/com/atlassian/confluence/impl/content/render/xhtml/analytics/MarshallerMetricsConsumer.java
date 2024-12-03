/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import java.util.function.Consumer;

public interface MarshallerMetricsConsumer
extends Consumer<MarshallerMetrics> {
    @Override
    public void accept(MarshallerMetrics var1);
}

