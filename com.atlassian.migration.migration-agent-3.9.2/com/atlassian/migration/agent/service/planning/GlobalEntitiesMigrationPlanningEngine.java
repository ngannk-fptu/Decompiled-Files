/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.dto.GlobalEntitiesExportStepConfig;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.planning.BaseStepPlanningEngine;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;

public class GlobalEntitiesMigrationPlanningEngine
extends BaseStepPlanningEngine<MigrateGlobalEntitiesTask> {
    public GlobalEntitiesMigrationPlanningEngine() {
        super(MigrateGlobalEntitiesTask.class, Arrays.asList(Pair.of((Object)StepType.GLOBAL_ENTITIES_EXPORT, (Object)35), Pair.of((Object)StepType.GLOBAL_ENTITIES_DATA_UPLOAD, (Object)30), Pair.of((Object)StepType.GLOBAL_ENTITIES_IMPORT, (Object)35)));
    }

    @Override
    public Step createFirstStep(MigrateGlobalEntitiesTask task) {
        Step step = new Step();
        step.setType(StepType.GLOBAL_ENTITIES_EXPORT.name());
        String cloudId = task.getPlan().getCloudSite().getCloudId();
        step.setConfig(Jsons.valueAsString(new GlobalEntitiesExportStepConfig(UUID.randomUUID().toString(), cloudId)));
        return step;
    }

    @Override
    public Optional<Step> createNextStep(MigrateGlobalEntitiesTask task, Step currentStep) {
        Step step = new Step();
        Optional<String> result = currentStep.getProgress().getResult();
        StepType stepType = StepType.valueOf(currentStep.getType());
        switch (stepType) {
            case GLOBAL_ENTITIES_EXPORT: {
                step.setType(StepType.GLOBAL_ENTITIES_DATA_UPLOAD.name());
                step.setConfig(result.orElseThrow(() -> new IllegalStateException("No result from export step")));
                return Optional.of(step);
            }
            case GLOBAL_ENTITIES_DATA_UPLOAD: {
                step.setType(StepType.GLOBAL_ENTITIES_IMPORT.name());
                step.setConfig(result.orElseThrow(() -> new IllegalStateException("No result from data upload step")));
                return Optional.of(step);
            }
            case GLOBAL_ENTITIES_IMPORT: {
                return Optional.empty();
            }
        }
        throw new IllegalStateException("Unknown step type " + currentStep.getType());
    }
}

