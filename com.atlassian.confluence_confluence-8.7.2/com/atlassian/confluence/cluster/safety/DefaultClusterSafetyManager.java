/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.safety.AbstractClusterSafetyManager;
import com.atlassian.confluence.cluster.safety.ClusterPanicAnalyticsEvent;
import com.atlassian.confluence.cluster.safety.ClusterPanicEvent;
import com.atlassian.confluence.cluster.safety.ClusterSafetyDao;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class DefaultClusterSafetyManager
extends AbstractClusterSafetyManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultClusterSafetyManager.class);
    @VisibleForTesting
    static final String SAFETY_NUMBER_MAP_NAME = DefaultClusterSafetyManager.class.getSimpleName() + ".safetyNumber";
    @VisibleForTesting
    static final String SAFETY_MODIFIER_MAP_NAME = DefaultClusterSafetyManager.class.getSimpleName() + ".safetyNumberModifier";
    private final SharedDataManager clusterSharedDataManager;

    public DefaultClusterSafetyManager(ClusterSafetyDao clusterSafetyDao, EventPublisher eventPublisher, SharedDataManager clusterSharedDataManager, ClusterManager clusterManager, LicenseService licenseService) {
        super(clusterSafetyDao, eventPublisher, clusterManager, licenseService);
        this.clusterSharedDataManager = Objects.requireNonNull(clusterSharedDataManager);
    }

    @Override
    protected void logRuntimeInfo() {
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected Map<String, String> getSafetyNumberModifierMap() {
        return this.clusterSharedDataManager.getSharedData(SAFETY_MODIFIER_MAP_NAME).getMap();
    }

    @Override
    protected Map<String, Integer> getSafetyNumberMap() {
        return this.clusterSharedDataManager.getSharedData(SAFETY_NUMBER_MAP_NAME).getMap();
    }

    @Override
    protected void handlePanic() {
        ConfluenceLicense currentLicense = this.getLicenseService().retrieve();
        this.getEventPublisher().publish((Object)new ClusterPanicAnalyticsEvent(false, 1, 1, currentLicense.getMaximumNumberOfUsers()));
        this.getEventPublisher().publish((Object)new ClusterPanicEvent(this, "Non Clustered Confluence: Database is being updated by another Confluence instance. Please see http://confluence.atlassian.com/x/mwiyCg for more details."));
    }
}

