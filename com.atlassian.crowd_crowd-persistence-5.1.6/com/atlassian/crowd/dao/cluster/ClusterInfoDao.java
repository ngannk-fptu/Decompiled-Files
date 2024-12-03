/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.cluster;

import com.atlassian.crowd.model.cluster.ClusterInfoEntity;
import java.util.Collection;

public interface ClusterInfoDao {
    public void store(ClusterInfoEntity var1);

    public Collection<ClusterInfoEntity> getNodeStatus(Collection<String> var1);

    public int removeStatusNotIn(Collection<String> var1);
}

