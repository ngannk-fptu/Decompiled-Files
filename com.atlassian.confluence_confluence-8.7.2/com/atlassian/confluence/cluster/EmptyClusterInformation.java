/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated(since="8.2", forRemoval=true)
public class EmptyClusterInformation
implements ClusterInformation {
    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public String getName() {
        return "(Cluster name not available: cluster is not running)";
    }

    @Override
    public String getDescription() {
        return "(Cluster description no available: cluster is not running)";
    }

    public List getMembers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public int getMemberCount() {
        return 0;
    }

    @Override
    public @Nullable ClusterJoinConfig getClusterJoinConfig() {
        return null;
    }
}

