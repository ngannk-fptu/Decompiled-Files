/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.impl.StepSubType;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.planning.BaseStepPlanningEngine;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class UsersMigrationPlanningEngine
extends BaseStepPlanningEngine<MigrateUsersTask> {
    public UsersMigrationPlanningEngine() {
        super(MigrateUsersTask.class, Collections.singletonList(Pair.of((Object)StepType.USERS_MIGRATION, (Object)100)), Collections.singletonList(Pair.of((Object)StepType.USERS_MIGRATION, Arrays.asList(Pair.of((Object)StepSubType.USERS_EXPORT, (Object)20), Pair.of((Object)StepSubType.USERS_UPLOAD, (Object)10), Pair.of((Object)StepSubType.USERS_IMPORT, (Object)70)))));
    }

    @Override
    public Step createFirstStep(MigrateUsersTask task) {
        Step step = new Step();
        step.setType(StepType.USERS_MIGRATION.name());
        step.setSubType(StepSubType.USERS_EXPORT.name());
        return step;
    }

    @Override
    public Optional<Step> createNextStep(MigrateUsersTask task, Step currentStep) {
        StepType stepType = StepType.valueOf(currentStep.getType());
        if (stepType == StepType.USERS_MIGRATION) {
            return Optional.empty();
        }
        throw new IllegalStateException("Unknown step type " + currentStep.getType());
    }
}

