/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.internal.api;

import com.atlassian.zdu.internal.api.NodeFinalizationInfo;
import com.atlassian.zdu.internal.api.NodeState;

public interface NodeInfo {
    public String getId();

    public String getName();

    public String getIpAddress();

    public int getPortNumber();

    public NodeState getState();

    public Integer getTasksTotal();

    public Integer getActiveUserCount();

    public String getBuildNumber();

    public String getVersion();

    public boolean isLocal();

    public NodeFinalizationInfo getFinalization();
}

