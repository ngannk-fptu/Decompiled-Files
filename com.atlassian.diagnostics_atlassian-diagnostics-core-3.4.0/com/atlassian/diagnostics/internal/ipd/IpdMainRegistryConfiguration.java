/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;

public interface IpdMainRegistryConfiguration {
    public String getProductPrefix();

    public boolean isIpdEnabled();

    public boolean isIpdWipEnabled();

    public void metricUpdated(IpdMetric var1);
}

