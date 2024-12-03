/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.service.cluster;

import com.atlassian.crowd.service.cluster.ClusterNode;
import java.util.Set;

public interface ClusterInformation {
    public Set<ClusterNode> getNodes();
}

