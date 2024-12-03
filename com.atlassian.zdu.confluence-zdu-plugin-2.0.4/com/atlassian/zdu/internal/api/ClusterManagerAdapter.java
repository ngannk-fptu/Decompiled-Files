/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.internal.api;

import com.atlassian.zdu.internal.api.NodeInfo;
import java.util.List;

public interface ClusterManagerAdapter {
    public List<NodeInfo> getNodes();

    public boolean isClustered();

    public boolean hasClusterFinalizationTasks();
}

