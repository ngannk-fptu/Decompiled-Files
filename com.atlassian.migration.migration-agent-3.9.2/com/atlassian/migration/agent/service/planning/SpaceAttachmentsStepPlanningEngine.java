/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.entity.SpaceAttachmentsOnlyTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.planning.BaseStepPlanningEngine;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class SpaceAttachmentsStepPlanningEngine
extends BaseStepPlanningEngine<SpaceAttachmentsOnlyTask> {
    public SpaceAttachmentsStepPlanningEngine() {
        super(SpaceAttachmentsOnlyTask.class, Collections.singletonList(Pair.of((Object)StepType.ATTACHMENT_UPLOAD, (Object)100)));
    }

    @Override
    public Step createFirstStep(SpaceAttachmentsOnlyTask task) {
        Step step = new Step();
        step.setType(StepType.ATTACHMENT_UPLOAD.name());
        step.setConfig(task.getSpaceKey());
        return step;
    }

    @Override
    public Optional<Step> createNextStep(SpaceAttachmentsOnlyTask task, Step currentStep) {
        if (StepType.valueOf(currentStep.getType()) == StepType.ATTACHMENT_UPLOAD) {
            return Optional.empty();
        }
        throw new IllegalStateException("Unknown step type " + currentStep.getType());
    }
}

