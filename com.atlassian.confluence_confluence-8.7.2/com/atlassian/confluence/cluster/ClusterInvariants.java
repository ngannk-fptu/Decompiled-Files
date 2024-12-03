/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.cluster;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.util.GeneralUtil;
import java.io.Serializable;

public class ClusterInvariants
implements Serializable {
    public static final long serialVersionUID = -7857801326247941479L;
    private final String versionNumber;

    public ClusterInvariants() {
        this(GeneralUtil.getVersionNumber());
    }

    @VisibleForTesting
    ClusterInvariants(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDifferenceExplanation(ClusterInvariants cluster) {
        if (!this.versionNumber.equals(cluster.versionNumber)) {
            return "the cluster is running version " + cluster.versionNumber + " of Confluence, while this node is running " + this.versionNumber;
        }
        throw new RuntimeException("Only call getDifferenceExplanation on non-equal nodes");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterInvariants that = (ClusterInvariants)o;
        return this.versionNumber.equals(that.versionNumber);
    }

    public int hashCode() {
        return this.versionNumber.hashCode();
    }
}

