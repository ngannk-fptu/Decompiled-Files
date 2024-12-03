/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.ClusterMonitoring
 *  com.atlassian.cluster.monitoring.spi.model.MonitoringError
 *  com.atlassian.cluster.monitoring.spi.model.NodeIdentifier
 *  com.atlassian.cluster.monitoring.spi.model.NodeInformation
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.plugin.ModuleCompleteKey
 *  io.atlassian.fugue.Either
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.nonclustered;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.ClusterMonitoring;
import com.atlassian.cluster.monitoring.spi.model.MonitoringError;
import com.atlassian.cluster.monitoring.spi.model.NodeIdentifier;
import com.atlassian.cluster.monitoring.spi.model.NodeInformation;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.plugin.ModuleCompleteKey;
import io.atlassian.fugue.Either;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class NonClusterMonitoring
implements ClusterMonitoring {
    private static final Logger log = LoggerFactory.getLogger(NonClusterMonitoring.class);
    static final String ENABLE_CLUSTERING_PROPERTY_KEY = "cluster.setup.ready";
    private final ApplicationConfiguration applicationConfig;
    private final LicenseService licenseService;

    public NonClusterMonitoring(ApplicationConfiguration applicationConfiguration, LicenseService licenseService) {
        this.applicationConfig = Objects.requireNonNull(applicationConfiguration);
        this.licenseService = Objects.requireNonNull(licenseService);
    }

    public Either<MonitoringError, List<NodeInformation>> getNodes() {
        throw new UnsupportedOperationException();
    }

    public Either<MonitoringError, NodeIdentifier> getCurrentNode() {
        throw new UnsupportedOperationException();
    }

    public Either<MonitoringError, Table> getData(ModuleCompleteKey key, NodeIdentifier nodeId) {
        throw new UnsupportedOperationException();
    }

    public boolean isAvailable() {
        return false;
    }

    public boolean isDataCenterLicensed() {
        return this.licenseService.isLicensedForDataCenter();
    }

    public boolean isClusterSetupEnabled() {
        try {
            this.applicationConfig.load();
        }
        catch (ConfigurationException e) {
            log.error("Error loading the config file to check for enabled clustering", (Throwable)e);
        }
        Object isClusterSetupEnabled = this.applicationConfig.getProperty((Object)ENABLE_CLUSTERING_PROPERTY_KEY);
        return "true".equals(isClusterSetupEnabled);
    }

    public boolean enableClustering() {
        if (this.isDataCenterLicensed()) {
            this.applicationConfig.setProperty((Object)ENABLE_CLUSTERING_PROPERTY_KEY, (Object)"true");
            try {
                this.applicationConfig.save();
            }
            catch (ConfigurationException e) {
                log.error("Error enabling clustering", (Throwable)e);
            }
            return true;
        }
        return false;
    }
}

