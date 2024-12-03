/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNullableByDefault
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNullableByDefault
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.annotations.nullability.ReturnValuesAreNullableByDefault;
import com.atlassian.confluence.cluster.UpgradeFinalizationRun;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

public class UpgradeFinalizationRunImpl
implements UpgradeFinalizationRun,
Serializable {
    private static final long serialVersionUID = -4068273635438706343L;
    private final long requestTimestamp;
    private final Long completedTimestamp;
    private final boolean clusterWide;
    private final List<UpgradeFinalizationRun.Error> errors;

    public UpgradeFinalizationRunImpl(long requestTimestamp, @Nullable Long completedTimestamp, boolean clusterWide, List<UpgradeFinalizationRun.Error> errors) {
        this.requestTimestamp = requestTimestamp;
        this.completedTimestamp = completedTimestamp;
        this.clusterWide = clusterWide;
        this.errors = errors;
    }

    @Override
    public long getRequestTimestamp() {
        return this.requestTimestamp;
    }

    @Override
    public Long getCompletedTimestamp() {
        return this.completedTimestamp;
    }

    @Override
    public List<UpgradeFinalizationRun.Error> getErrors() {
        return this.errors;
    }

    @Override
    public boolean runsClusterWideTasks() {
        return this.clusterWide;
    }

    @ParametersAreNullableByDefault
    @ReturnValuesAreNullableByDefault
    public static class ErrorImpl
    implements Serializable,
    UpgradeFinalizationRun.Error {
        private static final long serialVersionUID = 6419135469225483805L;
        private final String upgradeTaskName;
        private final String buildNumber;
        private final boolean isClusterWideTask;
        private final String exceptionMessage;
        private final Collection<String> upgradeErrors;

        public ErrorImpl(String upgradeTaskName, String buildNumber, boolean isClusterWideTask, String exceptionMessage, Collection<String> upgradeErrors) {
            this.upgradeTaskName = upgradeTaskName;
            this.buildNumber = buildNumber;
            this.isClusterWideTask = isClusterWideTask;
            this.exceptionMessage = exceptionMessage;
            this.upgradeErrors = upgradeErrors;
        }

        @Override
        public String getUpgradeTaskName() {
            return this.upgradeTaskName;
        }

        @Override
        public String getBuildNumber() {
            return this.buildNumber;
        }

        @Override
        public boolean isClusterWideTask() {
            return this.isClusterWideTask;
        }

        @Override
        public String getExceptionMessage() {
            return this.exceptionMessage;
        }

        @Override
        public Collection<String> getUpgradeErrors() {
            return this.upgradeErrors;
        }
    }
}

