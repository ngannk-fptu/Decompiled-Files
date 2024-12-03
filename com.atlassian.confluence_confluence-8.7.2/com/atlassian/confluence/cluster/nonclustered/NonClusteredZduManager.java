/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster.nonclustered;

import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.NodeZduInfo;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class NonClusteredZduManager
implements ZduManager {
    private static final String INSTANCE_IS_NOT_CLUSTERED = "Instance is not clustered";

    @Override
    public ZduStatus getUpgradeStatus() {
        return ZduStatus.disabled();
    }

    @Override
    public ZduStatus startUpgrade() {
        throw new IllegalStateException(INSTANCE_IS_NOT_CLUSTERED);
    }

    @Override
    public void endUpgrade() {
        throw new IllegalStateException(INSTANCE_IS_NOT_CLUSTERED);
    }

    @Override
    public void retryFinalization() {
        throw new IllegalStateException(INSTANCE_IS_NOT_CLUSTERED);
    }

    @Override
    public Map<ClusterNodeInformation, CompletionStage<NodeZduInfo>> getNodesZduInfo() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isPendingDatabaseFinalization() {
        return false;
    }
}

