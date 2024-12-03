/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.ConfExportStepConfig;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.planning.BaseStepPlanningEngine;
import com.atlassian.migration.agent.service.planning.StepPlanningEngine;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class ConfSpacePlanningEngine
extends BaseStepPlanningEngine<ConfluenceSpaceTask> {
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public ConfSpacePlanningEngine(MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        super(ConfluenceSpaceTask.class, Arrays.asList(Pair.of((Object)StepType.ATTACHMENT_UPLOAD, (Object)30), Pair.of((Object)StepType.CONFLUENCE_EXPORT, (Object)20), Pair.of((Object)StepType.SPACE_USERS_MIGRATION, (Object)10), Pair.of((Object)StepType.DATA_UPLOAD, (Object)20), Pair.of((Object)StepType.CONFLUENCE_IMPORT, (Object)20)));
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @Override
    public Step createFirstStep(ConfluenceSpaceTask task) {
        Step step = new Step();
        step.setType(StepType.ATTACHMENT_UPLOAD.name());
        step.setConfig(task.getSpaceKey());
        return step;
    }

    @Override
    public Optional<Step> createNextStep(ConfluenceSpaceTask task, Step currentStep) {
        Step step = new Step();
        Optional<String> result = currentStep.getProgress().getResult();
        StepType stepType = StepType.valueOf(currentStep.getType());
        switch (stepType) {
            case ATTACHMENT_UPLOAD: {
                step.setType(StepType.CONFLUENCE_EXPORT.name());
                String cloudId = task.getPlan().getCloudSite().getCloudId();
                step.setConfig(Jsons.valueAsString(new ConfExportStepConfig(task.getSpaceKey(), cloudId)));
                return Optional.of(step);
            }
            case CONFLUENCE_EXPORT: {
                if (this.migrationDarkFeaturesManager.isExportOnlyEnabled()) {
                    return Optional.empty();
                }
                step.setType(StepType.SPACE_USERS_MIGRATION.name());
                step.setConfig(result.orElseThrow(() -> new IllegalStateException("No result from export step")));
                return Optional.of(step);
            }
            case SPACE_USERS_MIGRATION: {
                step.setType(StepType.DATA_UPLOAD.name());
                step.setConfig(result.orElseThrow(() -> new IllegalStateException("No result from space users step")));
                return Optional.of(step);
            }
            case DATA_UPLOAD: {
                if (this.migrationDarkFeaturesManager.isUploadOnlyEnabled()) {
                    return Optional.empty();
                }
                step.setType(StepType.CONFLUENCE_IMPORT.name());
                step.setConfig(result.orElseThrow(() -> new IllegalStateException("No result from data upload step")));
                return Optional.of(step);
            }
            case CONFLUENCE_IMPORT: {
                return Optional.empty();
            }
        }
        throw new IllegalStateException("Unknown step type " + currentStep.getType());
    }

    @Override
    public Optional<StepPlanningEngine.PercentRange> getStepPercentRange(Step step) {
        if (this.migrationDarkFeaturesManager.isExportOnlyEnabled()) {
            ImmutableMap exportOnlyMap = ImmutableMap.of((Object)StepType.ATTACHMENT_UPLOAD.name(), (Object)new StepPlanningEngine.PercentRange(0, 50), (Object)StepType.CONFLUENCE_EXPORT.name(), (Object)new StepPlanningEngine.PercentRange(50, 100));
            return Optional.ofNullable(exportOnlyMap.get(step.getType()));
        }
        if (this.migrationDarkFeaturesManager.isUploadOnlyEnabled()) {
            ImmutableMap uploadOnlyMap = ImmutableMap.of((Object)StepType.ATTACHMENT_UPLOAD.name(), (Object)new StepPlanningEngine.PercentRange(0, 50), (Object)StepType.CONFLUENCE_EXPORT.name(), (Object)new StepPlanningEngine.PercentRange(50, 70), (Object)StepType.SPACE_USERS_MIGRATION.name(), (Object)new StepPlanningEngine.PercentRange(70, 80), (Object)StepType.DATA_UPLOAD.name(), (Object)new StepPlanningEngine.PercentRange(80, 100));
            return Optional.ofNullable(uploadOnlyMap.get(step.getType()));
        }
        return super.getStepPercentRange(step);
    }
}

