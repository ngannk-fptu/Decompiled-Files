/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import java.util.Map;

public interface NodeStatus {
    public Map<String, String> getJVMstats();

    public Map<String, String> getProps();

    public Map<String, String> getBuildStats();
}

