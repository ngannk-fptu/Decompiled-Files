/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.impl.StepTypeEnum;
import com.atlassian.migration.agent.service.planning.StepPlanningEngine;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public abstract class BaseStepPlanningEngine<T extends Task>
implements StepPlanningEngine<T> {
    private final Class<T> taskType;
    private final Map<String, StepPlanningEngine.PercentRange> percentRangeMap;
    private static final Logger log = ContextLoggerFactory.getLogger(BaseStepPlanningEngine.class);
    private final Map<String, Map<String, StepPlanningEngine.PercentRange>> subStepPercentageRangeMap;

    BaseStepPlanningEngine(Class<T> taskType, Collection<Pair<StepTypeEnum, Integer>> percentAtEnd, Collection<Pair<StepType, Collection<Pair<StepTypeEnum, Integer>>>> subStepPercentageRangeAtEnd) {
        this.taskType = taskType;
        this.percentRangeMap = BaseStepPlanningEngine.calculateStepPercentRangeMap(percentAtEnd);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Pair<StepType, Collection<Pair<StepTypeEnum, Integer>>> entry : subStepPercentageRangeAtEnd) {
            builder.put((Object)((StepType)entry.getKey()).name(), BaseStepPlanningEngine.calculateStepPercentRangeMap((Collection)entry.getValue()));
        }
        this.subStepPercentageRangeMap = builder.build();
    }

    BaseStepPlanningEngine(Class<T> taskType, Collection<Pair<StepTypeEnum, Integer>> percentAtEnd) {
        this(taskType, percentAtEnd, Collections.emptyList());
    }

    private static Map<String, StepPlanningEngine.PercentRange> calculateStepPercentRangeMap(Collection<Pair<StepTypeEnum, Integer>> percentAtEnd) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        int currentPercent = 0;
        for (Pair<StepTypeEnum, Integer> entry : percentAtEnd) {
            int to;
            int from = currentPercent;
            currentPercent = to = currentPercent + (Integer)entry.getRight();
            builder.put((Object)((StepTypeEnum)entry.getLeft()).name(), (Object)new StepPlanningEngine.PercentRange(from, to));
        }
        if (currentPercent != 100) {
            throw new IllegalArgumentException("Sum of all steps' percentages should be 100, current value " + currentPercent);
        }
        return builder.build();
    }

    @Override
    @Nonnull
    public Optional<StepPlanningEngine.PercentRange> getStepPercentRange(Step step) {
        return Optional.ofNullable(this.percentRangeMap.get(step.getType()));
    }

    @Override
    @Nonnull
    public Optional<StepPlanningEngine.PercentRange> getSubStepPercentRange(Step step) {
        try {
            if (!this.subStepPercentageRangeMap.isEmpty()) {
                return Optional.ofNullable(this.subStepPercentageRangeMap.getOrDefault(step.getType(), Collections.emptyMap()).getOrDefault(step.getSubType(), null));
            }
            return Optional.ofNullable(this.percentRangeMap.get(step.getSubType()));
        }
        catch (Exception e) {
            log.warn("Error reading sub step percentage for Step: {}", (Object)step.getType());
            return Optional.empty();
        }
    }

    @Override
    public Class<T> getTaskType() {
        return this.taskType;
    }
}

