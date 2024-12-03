/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceManager
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import java.util.Objects;
import java.util.function.Supplier;

public interface SpaceBoundStepExecutor
extends StepExecutor {
    public static final String SKIP_STEP_SPACE_NOT_FOUND_MSG_FORMAT = "Space %s is deleted or doesn't exist on your instance and therefore can't be migrated.";
    public static final String SKIPPED_RESULT = "SKIPPED";

    default public StepResult wrapStepResultSupplier(AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService, Step step, String spaceKey, SpaceManager spaceManager, Supplier<StepResult> stepResultSupplier) {
        if (Objects.nonNull(spaceManager.getSpace(spaceKey))) {
            return stepResultSupplier.get();
        }
        String spaceNotFoundReason = String.format(SKIP_STEP_SPACE_NOT_FOUND_MSG_FORMAT, spaceKey);
        analyticsEventService.saveAnalyticsEventAsync(() -> analyticsEventBuilder.buildStepSkipAnalyticEvent(step, spaceNotFoundReason));
        return StepResult.succeeded(spaceNotFoundReason, SKIPPED_RESULT);
    }
}

