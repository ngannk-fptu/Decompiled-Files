/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterInformation
 *  com.atlassian.confluence.cluster.ClusterJoinConfig
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import java.util.List;

public class RemoteClusterInformation
implements ClusterInformation {
    private boolean isRunning;
    private String name;
    private String description;
    private int memberCount;
    private ClusterJoinConfig clusterJoinConfig;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.cluster.ClusterInformation ci \n";

    public RemoteClusterInformation(ClusterInformation ci) {
        try {
            this.isRunning = ci.isRunning();
            this.name = ci.getName();
            this.description = ci.getDescription();
            this.memberCount = ci.getMemberCount();
            this.clusterJoinConfig = ci.getClusterJoinConfig();
        }
        catch (IllegalStateException ise) {
            this.isRunning = false;
            this.memberCount = 0;
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getMembers() {
        return null;
    }

    public int getMemberCount() {
        return this.memberCount;
    }

    public ClusterJoinConfig getClusterJoinConfig() {
        return this.clusterJoinConfig;
    }
}

