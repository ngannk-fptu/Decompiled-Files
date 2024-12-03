/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.metrics.MetricAdminMBean;
import com.amazonaws.metrics.MetricCollector;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.metrics.ServiceMetricCollector;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MetricAdmin
implements MetricAdminMBean {
    @Override
    public boolean enableDefaultMetrics() {
        return AwsSdkMetrics.enableDefaultMetrics();
    }

    @Override
    public void disableMetrics() {
        AwsSdkMetrics.disableMetrics();
    }

    @Override
    public String getRequestMetricCollector() {
        MetricCollector mc = AwsSdkMetrics.getInternalMetricCollector();
        RequestMetricCollector rmc = mc == null ? null : mc.getRequestMetricCollector();
        return mc == null || rmc == RequestMetricCollector.NONE ? "NONE" : rmc.getClass().getName();
    }

    @Override
    public String getServiceMetricCollector() {
        MetricCollector mc = AwsSdkMetrics.getInternalMetricCollector();
        ServiceMetricCollector smc = mc == null ? null : mc.getServiceMetricCollector();
        return mc == null || smc == ServiceMetricCollector.NONE ? "NONE" : smc.getClass().getName();
    }

    @Override
    public boolean isMetricsEnabled() {
        return AwsSdkMetrics.isMetricsEnabled();
    }

    @Override
    public boolean isMachineMetricsExcluded() {
        return AwsSdkMetrics.isMachineMetricExcluded();
    }

    @Override
    public void setMachineMetricsExcluded(boolean excludeJvmMetrics) {
        AwsSdkMetrics.setMachineMetricsExcluded(excludeJvmMetrics);
    }

    @Override
    public String getRegion() {
        return AwsSdkMetrics.getRegionName();
    }

    @Override
    public void setRegion(String region) {
        AwsSdkMetrics.setRegion(region);
    }

    @Override
    public Integer getMetricQueueSize() {
        return AwsSdkMetrics.getMetricQueueSize();
    }

    @Override
    public void setMetricQueueSize(Integer metricQueueSize) {
        AwsSdkMetrics.setMetricQueueSize(metricQueueSize);
    }

    @Override
    public Integer getQueuePollTimeoutMilli() {
        Long queuePollTimeoutMilli = AwsSdkMetrics.getQueuePollTimeoutMilli();
        return queuePollTimeoutMilli == null ? null : Integer.valueOf(queuePollTimeoutMilli.intValue());
    }

    @Override
    public void setQueuePollTimeoutMilli(Integer timeoutMilli) {
        AwsSdkMetrics.setQueuePollTimeoutMilli(timeoutMilli == null ? null : Long.valueOf(timeoutMilli.longValue()));
    }

    @Override
    public String getMetricNameSpace() {
        return AwsSdkMetrics.getMetricNameSpace();
    }

    @Override
    public void setMetricNameSpace(String metricNameSpace) {
        AwsSdkMetrics.setMetricNameSpace(metricNameSpace);
    }

    @Override
    public boolean isPerHostMetricsIncluded() {
        return AwsSdkMetrics.isPerHostMetricIncluded();
    }

    @Override
    public void setPerHostMetricsIncluded(boolean includePerHostMetrics) {
        AwsSdkMetrics.setPerHostMetricsIncluded(includePerHostMetrics);
    }

    @Override
    public String getJvmMetricName() {
        return AwsSdkMetrics.getJvmMetricName();
    }

    @Override
    public void setJvmMetricName(String jvmMetricName) {
        AwsSdkMetrics.setJvmMetricName(jvmMetricName);
    }

    @Override
    public String getHostMetricName() {
        return AwsSdkMetrics.getHostMetricName();
    }

    @Override
    public void setHostMetricName(String hostMetricName) {
        AwsSdkMetrics.setHostMetricName(hostMetricName);
    }

    @Override
    public String getCredentialFile() {
        return AwsSdkMetrics.getCredentialFile();
    }

    @Override
    public void setCredentialFile(String filepath) throws FileNotFoundException, IOException {
        AwsSdkMetrics.setCredentialFile(filepath);
    }

    @Override
    public boolean isSingleMetricNamespace() {
        return AwsSdkMetrics.isSingleMetricNamespace();
    }

    @Override
    public void setSingleMetricNamespace(boolean singleMetricNamespace) {
        AwsSdkMetrics.setSingleMetricNamespace(singleMetricNamespace);
    }
}

