/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.KubernetesClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;

public interface ClusterJoinConfig {
    default public ClusterJoinType getType() {
        return ClusterJoinType.NONE;
    }

    default public void decode(Decoder decoder) {
    }

    public static enum ClusterJoinType {
        NONE("None"),
        MULTICAST("Multicast"),
        TCP_IP("TCP/IP"),
        AWS("AWS"),
        KUBERNETES("Kubernetes");

        private String humanReadableName;

        private ClusterJoinType(String humanReadableName) {
            this.humanReadableName = humanReadableName;
        }

        public String getHumanReadableName() {
            return this.humanReadableName;
        }

        public String getText() {
            return this.name().toLowerCase();
        }

        public static ClusterJoinType fromString(String text) {
            return ClusterJoinType.valueOf(text.toUpperCase());
        }
    }

    public static interface Decoder {
        public void accept(TCPIPClusterJoinConfig var1);

        public void accept(MulticastClusterJoinConfig var1);

        public void accept(AWSClusterJoinConfig var1);

        public void accept(KubernetesClusterJoinConfig var1);
    }
}

