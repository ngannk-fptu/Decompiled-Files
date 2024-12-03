/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.crowd.service.cluster;

import com.atlassian.annotations.ExperimentalApi;
import java.time.Duration;
import java.util.Optional;

@ExperimentalApi
public interface ClusterNodeDetails {
    public String getIpAddress();

    public String getHostname();

    public Long getCurrentHeapSize();

    public Long getMaxHeapSize();

    public Optional<Double> getAverageLoad();

    public Duration getUptime();
}

