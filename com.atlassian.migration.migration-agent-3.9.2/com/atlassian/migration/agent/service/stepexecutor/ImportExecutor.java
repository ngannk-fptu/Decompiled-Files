/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.stepexecutor;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class ImportExecutor
implements StepExecutor {
    protected static final Duration POLLING_PERIOD = Duration.ofSeconds(5L);
    protected final ProgressTracker progressTracker;
    protected final StepStore stepStore;
    protected final PluginTransactionTemplate ptx;
    protected final AnalyticsEventService analyticsEventService;
    protected final AnalyticsEventBuilder analyticsEventBuilder;
    protected final Supplier<Instant> instantSupplier;
    protected final MigrationAgentConfiguration migrationAgentConfiguration;

    protected ImportExecutor(ProgressTracker progressTracker, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        this(progressTracker, stepStore, ptx, analyticsEventService, analyticsEventBuilder, Instant::now, migrationAgentConfiguration);
    }

    @VisibleForTesting
    protected ImportExecutor(ProgressTracker progressTracker, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, Supplier<Instant> instantSupplier, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.progressTracker = progressTracker;
        this.stepStore = stepStore;
        this.ptx = ptx;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.instantSupplier = instantSupplier;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    protected long getStepTime(Step step) {
        Progress progress = step.getProgress();
        if (progress != null && progress.getStartTime().isPresent()) {
            return this.instantSupplier.get().toEpochMilli() - progress.getStartTime().get().toEpochMilli();
        }
        return -1L;
    }

    protected boolean stepIsInCompleteStatus(Step step) {
        return step.getProgress().getStatus().isCompleted();
    }

    @VisibleForTesting
    protected abstract String initiateImport(String var1);

    @VisibleForTesting
    protected abstract Optional<StepResult> doProgressCheck(Step var1, String var2);
}

