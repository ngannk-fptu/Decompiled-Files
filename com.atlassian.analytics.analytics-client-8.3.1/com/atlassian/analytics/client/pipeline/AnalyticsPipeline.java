/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline;

public interface AnalyticsPipeline {
    public boolean canHandle(Object var1);

    public void process(Object var1);
}

