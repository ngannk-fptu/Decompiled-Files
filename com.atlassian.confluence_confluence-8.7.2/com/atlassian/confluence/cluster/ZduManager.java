/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.NodeZduInfo;
import com.atlassian.confluence.cluster.ZduStatus;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ZduManager {
    @Transactional(readOnly=true)
    public ZduStatus getUpgradeStatus();

    public ZduStatus startUpgrade();

    public void endUpgrade();

    @Transactional(readOnly=true)
    public Map<ClusterNodeInformation, CompletionStage<NodeZduInfo>> getNodesZduInfo();

    public void retryFinalization();

    @Transactional(readOnly=true)
    public boolean isPendingDatabaseFinalization();
}

