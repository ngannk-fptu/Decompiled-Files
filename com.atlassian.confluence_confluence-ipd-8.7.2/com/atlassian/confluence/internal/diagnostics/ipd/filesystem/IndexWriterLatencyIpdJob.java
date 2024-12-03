/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCopyMetric
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.filesystem;

import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCopyMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexWriterLatencyIpdJob
implements IpdJob {
    private static final Logger LOG = LoggerFactory.getLogger(IndexWriterLatencyIpdJob.class);
    private static final String COMMIT_JMX_OBJECT_NAME = "com.atlassian.confluence:type=metrics,category00=Others,category01=LuceneConnection,name=Commit";
    private static final String IPD_METRIC_NAME = "home.local.write.latency.indexwriter.statistics";
    private IpdCopyMetric metric;

    public IndexWriterLatencyIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMetricRegistry) {
        try {
            ObjectName originalObjectName = new ObjectName(COMMIT_JMX_OBJECT_NAME);
            this.metric = (IpdCopyMetric)ipdMetricRegistry.register(IpdCopyMetric.builder((String)IPD_METRIC_NAME, (ObjectName)originalObjectName, (List)IpdStatsMetric.allAttributes, (List)IpdStatsMetric.shortAttributes, (MetricTag.RequiredMetricTag[])new MetricTag.RequiredMetricTag[0]).asWorkInProgress());
            ipdJobRunner.register((IpdJob)this);
        }
        catch (MalformedObjectNameException e) {
            LOG.warn(String.format("Metric is not created. The string passed as a parameter %s does not have the right format.", COMMIT_JMX_OBJECT_NAME), (Throwable)e);
        }
    }

    public void runJob() {
        if (this.metric != null) {
            this.metric.update();
        }
    }

    public boolean isWorkInProgressJob() {
        return true;
    }
}

