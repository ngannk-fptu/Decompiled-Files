/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.cluster;

import net.sf.ehcache.cluster.ClusterNode;

public interface ClusterTopologyListener {
    public void nodeJoined(ClusterNode var1);

    public void nodeLeft(ClusterNode var1);

    public void clusterOnline(ClusterNode var1);

    public void clusterOffline(ClusterNode var1);

    public void clusterRejoined(ClusterNode var1, ClusterNode var2);
}

