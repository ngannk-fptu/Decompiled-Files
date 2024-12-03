/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.MetricKey
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricKey;
import java.util.List;
import javax.management.ObjectName;

public interface IpdMetric {
    public MetricKey getMetricKey();

    public MetricOptions getOptions();

    public ObjectName getObjectName();

    public boolean isEnabled();

    public List<IpdMetricValue> readValues(boolean var1);

    public void unregisterJmx();

    public void close();
}

