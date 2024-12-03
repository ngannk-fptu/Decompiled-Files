/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterJoinConfig;

public class KubernetesClusterJoinConfig
implements ClusterJoinConfig {
    @Override
    public ClusterJoinConfig.ClusterJoinType getType() {
        return ClusterJoinConfig.ClusterJoinType.KUBERNETES;
    }

    @Override
    public void decode(ClusterJoinConfig.Decoder decoder) {
        decoder.accept(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return 0;
    }
}

