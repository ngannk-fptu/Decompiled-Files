/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.cluster.NodeZduInfo;
import com.atlassian.confluence.cluster.UpgradeFinalizationRun;
import com.atlassian.confluence.server.ApplicationState;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class NodeZduInfoImpl
implements NodeZduInfo,
Serializable {
    private static final long serialVersionUID = -5503630828327173853L;
    private final ApplicationState state;
    private final String buildNumber;
    private final String version;
    private final int longRunningTaskCount;
    private final int activeUserCount;
    private final boolean hasFinalizationTasks;
    private final UpgradeFinalizationRun finalizationRun;

    public NodeZduInfoImpl(ApplicationState state, String buildNumber, String version, int longRunningTaskCount, int activeUserCount, boolean hasFinalizationTasks, @Nullable UpgradeFinalizationRun finalizationRun) {
        this.state = Objects.requireNonNull(state);
        this.buildNumber = Objects.requireNonNull(buildNumber);
        this.version = Objects.requireNonNull(version);
        this.longRunningTaskCount = longRunningTaskCount;
        this.activeUserCount = activeUserCount;
        this.hasFinalizationTasks = hasFinalizationTasks;
        this.finalizationRun = finalizationRun;
    }

    @Override
    public ApplicationState getApplicationState() {
        return this.state;
    }

    @Override
    public String getBuildNumber() {
        return this.buildNumber;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public int getLongRunningTaskCount() {
        return this.longRunningTaskCount;
    }

    @Override
    public int getActiveUserCount() {
        return this.activeUserCount;
    }

    @Override
    public boolean isPendingLocalFinalization() {
        return this.hasFinalizationTasks;
    }

    @Override
    public Optional<UpgradeFinalizationRun> getFinalizationRun() {
        return Optional.ofNullable(this.finalizationRun);
    }
}

