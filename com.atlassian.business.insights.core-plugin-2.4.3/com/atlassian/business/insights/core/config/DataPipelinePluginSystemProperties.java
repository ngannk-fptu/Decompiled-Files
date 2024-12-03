/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.business.insights.core.config;

public class DataPipelinePluginSystemProperties {
    public static final String EXPORT_EXECUTOR_WAIT_TIMEOUT_SECONDS = "plugin.data-pipeline.export.executor.waitForCompletion.timeout.seconds";
    public static final int EXPORT_EXECUTOR_WAIT_TIMEOUT_SECONDS_DEFAULT = 5;
    public static final String EXPORT_EXECUTOR_TERMINATE_TIMEOUT_SECONDS = "plugin.data-pipeline.export.executor.termination.timeout.seconds";
    public static final int EXPORT_EXECUTOR_TERMINATE_TIMEOUT_SECONDS_DEFAULT = 120;

    public int getExportExecutorWaitTimeoutSeconds() {
        return Integer.getInteger(EXPORT_EXECUTOR_WAIT_TIMEOUT_SECONDS, 5);
    }

    public int getExportExecutorTerminateTimeoutSeconds() {
        return Integer.getInteger(EXPORT_EXECUTOR_TERMINATE_TIMEOUT_SECONDS, 120);
    }
}

