/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.plugin.manager;

import com.atlassian.annotations.PublicSpi;

@PublicSpi
public interface ClusterEnvironmentProvider {
    public static final ClusterEnvironmentProvider SINGLE_NODE = () -> false;

    public boolean isInCluster();
}

