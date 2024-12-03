/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.MetricTag
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.userdirectory;

import com.atlassian.confluence.internal.diagnostics.ipd.metric.type.IpdConnectionStateType;
import com.atlassian.confluence.internal.diagnostics.ipd.userdirectory.service.UserDirectoryConnectionService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtUserDirectoryConnectionStateIpdJob
implements IpdJob {
    public static final String USER_DIRECTORY_CONNECTION_STATE_METRIC_KEY = "user.directory.connection.state";
    private final IpdMetricRegistry ipdMetricRegistry;
    private final UserDirectoryConnectionService userDirectoryConnectionService;
    private Set<MetricKey> lastUserDirMetrics;

    public ExtUserDirectoryConnectionStateIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, UserDirectoryConnectionService userDirectoryConnectionService) {
        this.ipdMetricRegistry = ipdMainRegistry.createRegistry(IpdMetricBuilder::asWorkInProgress);
        this.userDirectoryConnectionService = userDirectoryConnectionService;
        this.lastUserDirMetrics = new HashSet<MetricKey>();
        ipdJobRunner.register((IpdJob)this);
    }

    public void runJob() {
        this.removeObsoleteMetrics();
        this.generateMetrics();
    }

    private void removeObsoleteMetrics() {
        Set currentMetricKeys = this.userDirectoryConnectionService.findAllActiveExternalDirectories().map(ExtUserDirectoryConnectionStateIpdJob::getBaseMetricKey).collect(Collectors.toSet());
        this.lastUserDirMetrics.stream().filter(lastKey -> !currentMetricKeys.contains(lastKey)).forEach(baseMetricKey -> this.ipdMetricRegistry.remove(this.getMetric((MetricKey)baseMetricKey).getMetricKey()));
        this.lastUserDirMetrics = currentMetricKeys;
    }

    private void generateMetrics() {
        this.userDirectoryConnectionService.findAllActiveExternalDirectories().forEach(directory -> {
            IpdCustomMetric<IpdConnectionStateType> metric = this.getMetric((Directory)directory);
            metric.update(m -> m.setConnected(this.userDirectoryConnectionService.getConnectionState((Directory)directory)));
        });
    }

    private static MetricKey getBaseMetricKey(Directory directory) {
        return MetricKey.metricKey((String)USER_DIRECTORY_CONNECTION_STATE_METRIC_KEY, (MetricTag.RequiredMetricTag[])new MetricTag.RequiredMetricTag[]{MetricTag.of((String)"userDirName", (String)directory.getName())});
    }

    private IpdCustomMetric<IpdConnectionStateType> getMetric(Directory directory) {
        MetricKey key = ExtUserDirectoryConnectionStateIpdJob.getBaseMetricKey(directory);
        return this.getMetric(key);
    }

    private IpdCustomMetric<IpdConnectionStateType> getMetric(MetricKey key) {
        return this.ipdMetricRegistry.customMetric(key.getMetricName(), IpdConnectionStateType.class, key.getTags().toArray(new MetricTag.RequiredMetricTag[0]));
    }

    public boolean isWorkInProgressJob() {
        return true;
    }
}

