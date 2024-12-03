/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface MetricAdminMBean {
    public boolean isMetricsEnabled();

    public String getRequestMetricCollector();

    public String getServiceMetricCollector();

    public boolean enableDefaultMetrics();

    public void disableMetrics();

    public boolean isMachineMetricsExcluded();

    public void setMachineMetricsExcluded(boolean var1);

    public boolean isPerHostMetricsIncluded();

    public void setPerHostMetricsIncluded(boolean var1);

    public String getRegion();

    public void setRegion(String var1);

    public String getCredentialFile();

    public void setCredentialFile(String var1) throws FileNotFoundException, IOException;

    public Integer getMetricQueueSize();

    public void setMetricQueueSize(Integer var1);

    public Integer getQueuePollTimeoutMilli();

    public void setQueuePollTimeoutMilli(Integer var1);

    public String getMetricNameSpace();

    public void setMetricNameSpace(String var1);

    public String getJvmMetricName();

    public void setJvmMetricName(String var1);

    public String getHostMetricName();

    public void setHostMetricName(String var1);

    public boolean isSingleMetricNamespace();

    public void setSingleMetricNamespace(boolean var1);
}

