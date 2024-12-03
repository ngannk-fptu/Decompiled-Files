/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.crowd.service.cluster;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.service.cluster.ClusterMessageListener;

@ExperimentalApi
public interface ClusterMessageService {
    public void registerListener(ClusterMessageListener var1, String var2);

    public void unregisterListener(ClusterMessageListener var1, String var2);

    public void unregisterListener(ClusterMessageListener var1);

    public void publish(String var1, String var2);
}

