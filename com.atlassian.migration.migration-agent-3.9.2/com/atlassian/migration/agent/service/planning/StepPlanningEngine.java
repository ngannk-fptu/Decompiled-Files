/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import java.util.Optional;

public interface StepPlanningEngine<T extends Task> {
    public Step createFirstStep(T var1);

    public Optional<Step> createNextStep(T var1, Step var2);

    public Optional<PercentRange> getStepPercentRange(Step var1);

    public Optional<PercentRange> getSubStepPercentRange(Step var1);

    public Class<T> getTaskType();

    public static class PercentRange {
        public final int from;
        public final int to;

        public PercentRange(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public boolean isFirst() {
            return this.from == 0;
        }

        public boolean isLast() {
            return this.to == 100;
        }
    }
}

