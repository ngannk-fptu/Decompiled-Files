/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.hibernate.Hibernate
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.service.planning.StepPlanningEngine;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.hibernate.Hibernate;

@ParametersAreNonnullByDefault
public class StepPlanningEngines {
    private final Map<Class<? extends Task>, StepPlanningEngine<? extends Task>> engines;

    public StepPlanningEngines(List<StepPlanningEngine<? extends Task>> engines) {
        this.engines = engines.stream().collect(Collectors.toMap(StepPlanningEngine::getTaskType, Function.identity()));
    }

    @Nonnull
    public Optional<StepPlanningEngine<? extends Task>> of(Task task) {
        return Optional.ofNullable(this.engines.get(Hibernate.getClass((Object)task)));
    }
}

