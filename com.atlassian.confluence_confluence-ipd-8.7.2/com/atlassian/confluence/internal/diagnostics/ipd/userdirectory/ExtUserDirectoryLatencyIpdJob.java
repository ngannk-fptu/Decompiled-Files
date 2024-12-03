/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.MetricTag
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.userdirectory;

import com.atlassian.confluence.internal.diagnostics.ipd.userdirectory.service.UserDirectoryConnectionService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtUserDirectoryLatencyIpdJob
implements IpdJob {
    public static final String USER_DIRECTORY_LATENCY_METRIC_KEY = "user.directory.connection.latency";
    private final IpdMetricRegistry ipdMetricRegistry;
    private final UserDirectoryConnectionService userDirectoryConnectionService;
    private Set<MetricKey> lastUserDirMetrics;

    public ExtUserDirectoryLatencyIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, UserDirectoryConnectionService userDirectoryConnectionService) {
        this.ipdMetricRegistry = ipdMainRegistry.createRegistry(IpdMetricBuilder::asWorkInProgress);
        this.userDirectoryConnectionService = userDirectoryConnectionService;
        this.lastUserDirMetrics = new HashSet<MetricKey>();
        ipdJobRunner.register((IpdJob)this);
    }

    public void runJob() {
        this.removeObsoleteMetrics();
        this.generateMetrics();
    }

    private void generateMetrics() {
        this.userDirectoryConnectionService.findAllActiveExternalDirectories().forEach(dir -> {
            IpdValueAndStatsMetricWrapper valueAndStatsMetric = this.getMetric((Directory)dir);
            Optional<Duration> latency = this.userDirectoryConnectionService.getLatency((Directory)dir);
            long value = latency.map(Duration::toMillis).orElse(-1L);
            if (latency.isPresent()) {
                valueAndStatsMetric.update(Long.valueOf(value));
            } else {
                valueAndStatsMetric.updateValue(Long.valueOf(value));
            }
        });
    }

    public void removeObsoleteMetrics() {
        Set currentMetricKeys = this.userDirectoryConnectionService.findAllActiveExternalDirectories().map(this::getBaseMetricKey).collect(Collectors.toSet());
        this.lastUserDirMetrics.stream().filter(lastKey -> !currentMetricKeys.contains(lastKey)).forEach(key -> {
            this.ipdMetricRegistry.remove(MetricKey.metricKey((String)key.getMetricName().concat(".value"), (Collection)key.getTags()));
            this.ipdMetricRegistry.remove(MetricKey.metricKey((String)key.getMetricName().concat(".statistics"), (Collection)key.getTags()));
        });
        this.lastUserDirMetrics = currentMetricKeys;
    }

    private MetricKey getBaseMetricKey(Directory directory) {
        return MetricKey.metricKey((String)USER_DIRECTORY_LATENCY_METRIC_KEY, (MetricTag.RequiredMetricTag[])new MetricTag.RequiredMetricTag[]{MetricTag.of((String)"userDirName", (String)directory.getName())});
    }

    private IpdValueAndStatsMetricWrapper getMetric(Directory directory) {
        return this.ipdMetricRegistry.valueAndStatsMetric(USER_DIRECTORY_LATENCY_METRIC_KEY, new MetricTag.RequiredMetricTag[]{MetricTag.of((String)"userDirName", (String)directory.getName())});
    }

    public boolean isWorkInProgressJob() {
        return true;
    }
}

