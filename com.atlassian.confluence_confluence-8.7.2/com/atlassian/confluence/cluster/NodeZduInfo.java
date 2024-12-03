/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.UpgradeFinalizationRun;
import com.atlassian.confluence.server.ApplicationState;
import java.util.Optional;

public interface NodeZduInfo {
    public ApplicationState getApplicationState();

    public String getBuildNumber();

    public String getVersion();

    public int getLongRunningTaskCount();

    public int getActiveUserCount();

    public boolean isPendingLocalFinalization();

    public Optional<UpgradeFinalizationRun> getFinalizationRun();
}

