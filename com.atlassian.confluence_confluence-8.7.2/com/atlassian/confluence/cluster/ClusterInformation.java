/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterJoinConfig;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated(since="8.2", forRemoval=true)
public interface ClusterInformation {
    public boolean isRunning();

    public String getName();

    public String getDescription();

    public List<String> getMembers();

    public int getMemberCount();

    public @Nullable ClusterJoinConfig getClusterJoinConfig();
}

