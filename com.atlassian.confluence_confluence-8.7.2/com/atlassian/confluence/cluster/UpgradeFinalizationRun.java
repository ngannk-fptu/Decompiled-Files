/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import java.util.Collection;
import java.util.List;

public interface UpgradeFinalizationRun {
    public long getRequestTimestamp();

    public Long getCompletedTimestamp();

    public List<Error> getErrors();

    default public boolean isFailed() {
        return !this.getErrors().isEmpty();
    }

    default public boolean isCompleted() {
        return this.getCompletedTimestamp() != null;
    }

    public boolean runsClusterWideTasks();

    public static interface Error {
        public String getUpgradeTaskName();

        public String getBuildNumber();

        public boolean isClusterWideTask();

        public String getExceptionMessage();

        public Collection<String> getUpgradeErrors();
    }
}

