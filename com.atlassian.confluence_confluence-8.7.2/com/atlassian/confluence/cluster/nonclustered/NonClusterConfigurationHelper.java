/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfig
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.cluster.nonclustered;

import com.atlassian.config.ApplicationConfig;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.setup.BootstrapDatabaseAccessor;
import com.google.common.base.Preconditions;
import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NonClusterConfigurationHelper
implements ClusterConfigurationHelperInternal {
    private final ApplicationConfig applicationConfig;

    public NonClusterConfigurationHelper(ApplicationConfig applicationConfig) {
        this.applicationConfig = (ApplicationConfig)Preconditions.checkNotNull((Object)applicationConfig);
    }

    @Override
    public boolean isClusteredInstance() {
        return false;
    }

    @Override
    public boolean isClusterHomeConfigured() {
        return false;
    }

    @Override
    public void createCluster(String clusterName, File clusterHome, String networkInterfaceName, ClusterJoinConfig joinConfig) throws ClusterException {
        throw new IllegalStateException("Clustering not available");
    }

    @Override
    public void bootstrapCluster(BootstrapDatabaseAccessor.BootstrapDatabaseData bootstrapDatabaseData) {
        File sharedHome = this.sharedHome().get();
        if (!sharedHome.exists() && !sharedHome.mkdir()) {
            throw new IllegalStateException("Failed to create shared home directory in " + sharedHome);
        }
    }

    @Override
    public List<NetworkInterface> getClusterableInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public Optional<File> sharedHome() {
        return Optional.of(new File(this.applicationConfig.getApplicationHome(), "shared-home"));
    }

    @Override
    public Optional<ClusterJoinConfig> joinConfig() {
        return Optional.empty();
    }

    @Override
    public void createSharedHome() {
    }

    @Override
    public void saveSetupConfigIntoSharedHome() {
    }

    @Override
    public void populateExistingClusterSetupConfig() {
    }

    @Override
    public void createClusterConfig() {
    }

    @Override
    public void saveSharedProperty(Object key, Object value) {
        throw new IllegalStateException("Clustering not available");
    }

    @Override
    public Optional<Object> getSharedProperty(Object key) {
        throw new IllegalStateException("Clustering not available");
    }

    @Override
    public void saveSharedBuildNumber(String sharedBuildNumber) {
        throw new IllegalStateException("Clustering not available");
    }

    @Override
    public Optional<String> getSharedBuildNumber() {
        throw new IllegalStateException("Clustering not available");
    }
}

