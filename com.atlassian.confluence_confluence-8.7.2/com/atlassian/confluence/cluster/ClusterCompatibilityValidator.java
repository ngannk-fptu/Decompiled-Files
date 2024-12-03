/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.upgrade.VersionNumberComparator
 *  com.google.common.collect.Comparators
 */
package com.atlassian.confluence.cluster;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterInvariants;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.impl.setup.BootstrapDatabaseAccessor;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.upgrade.VersionNumberComparator;
import com.google.common.collect.Comparators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ClusterCompatibilityValidator {
    private final ClusterManager clusterManager;
    private final ClusterInvariants currentInvariants;
    private final String zduMinVersion;
    private final String currentVersion;

    public ClusterCompatibilityValidator(ClusterManager clusterManager) {
        this(clusterManager, new ClusterInvariants(), BuildInformation.INSTANCE.getVersionNumber(), BuildInformation.INSTANCE.getZduMinVersion());
    }

    @VisibleForTesting
    ClusterCompatibilityValidator(ClusterManager clusterManager, ClusterInvariants clusterInvariants, String currentVersion, String zduMinVersion) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.currentInvariants = clusterInvariants;
        this.zduMinVersion = Objects.requireNonNull(zduMinVersion);
        this.currentVersion = Objects.requireNonNull(currentVersion);
    }

    public void validate(BootstrapDatabaseAccessor.BootstrapDatabaseData data) throws ClusterException {
        ZduStatus status = data.getZduStatus();
        if (status.getState() == ZduStatus.State.ENABLED) {
            String clusterVersion = status.getOriginalClusterVersion().orElseThrow(() -> new IllegalArgumentException("ZDU is enabled but missing originalClusterVersion"));
            if (!Comparators.isInOrder(Arrays.asList(this.zduMinVersion, clusterVersion, this.currentVersion), (Comparator)VersionNumberComparator.INSTANCE)) {
                throw new ClusterException(String.format("Cannot perform rolling upgrade from %s to this version (%s). This version of Confluence can only be rolling-upgraded from a cluster running a version between %s and %s.", clusterVersion, this.currentVersion, this.zduMinVersion, this.currentVersion));
            }
        } else {
            ClusterInvariants cluster = this.clusterManager.getClusterInvariants();
            if (cluster != null && !this.currentInvariants.equals(cluster)) {
                throw new ClusterException(this.currentInvariants.getDifferenceExplanation(cluster));
            }
        }
    }
}

