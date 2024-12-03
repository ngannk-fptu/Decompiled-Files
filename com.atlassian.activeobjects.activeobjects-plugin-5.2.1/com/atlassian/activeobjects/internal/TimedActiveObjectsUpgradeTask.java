/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  javax.annotation.Nonnull
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.util.Objects;
import javax.annotation.Nonnull;

public class TimedActiveObjectsUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final String DB_AO_UPGRADE_TASK_TIMER_NAME = "db.ao.upgradeTask";
    private static final String TASK_NAME = "taskName";
    private final ActiveObjectsUpgradeTask activeObjectsUpgradeTask;
    private final String pluginKey;

    public TimedActiveObjectsUpgradeTask(@Nonnull ActiveObjectsUpgradeTask activeObjectsUpgradeTask, @Nonnull String pluginKey) {
        this.activeObjectsUpgradeTask = Objects.requireNonNull(activeObjectsUpgradeTask, "activeObjectsUpgradeTask");
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
    }

    @Override
    public ModelVersion getModelVersion() {
        return this.activeObjectsUpgradeTask.getModelVersion();
    }

    @Override
    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        try (Ticker ignored = Metrics.metric((String)DB_AO_UPGRADE_TASK_TIMER_NAME).tag(TASK_NAME, this.activeObjectsUpgradeTask.getClass().getCanonicalName()).fromPluginKey(this.pluginKey).withAnalytics().startLongRunningTimer();){
            this.activeObjectsUpgradeTask.upgrade(currentVersion, ao);
        }
    }
}

