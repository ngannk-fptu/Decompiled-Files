/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.ConsentRequest
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.dto.assessment.MarketplaceSharedConsentDto;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.app.DefaultAppMigrationServiceClient;
import com.atlassian.migration.app.dto.ConsentRequest;
import java.util.ArrayList;
import java.util.Optional;
import java.util.SortedSet;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConsentService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppConsentService.class);
    private boolean isConsentedOnCloud = false;
    private final DefaultAppMigrationServiceClient appMigrationServiceClient;
    private final CloudSiteService cloudSiteService;
    private final SortedSet<String> sens;

    public AppConsentService(DefaultAppMigrationServiceClient appMigrationServiceClient, CloudSiteService cloudSiteService, SENSupplier senSupplier) {
        this.appMigrationServiceClient = appMigrationServiceClient;
        this.cloudSiteService = cloudSiteService;
        this.sens = senSupplier.getSens();
    }

    public MarketplaceSharedConsentDto getSharedConsentForMarketplacePartners() {
        return MarketplaceSharedConsentDto.builder().shouldCollectConsent(this.shouldCollectConsent()).build();
    }

    public MarketplaceSharedConsentDto saveSharedConsentForMarketplacePartners(String displayedText) {
        if (!this.shouldCollectConsent()) {
            throw new IllegalStateException("Consent should not be collected");
        }
        String cloudSiteId = this.getLinkedCloudSiteId().orElseThrow(() -> new IllegalArgumentException("No linked sites available"));
        this.isConsentedOnCloud = this.appMigrationServiceClient.saveConsent(cloudSiteId, "allowAppMigrationsDataShare", new ConsentRequest(displayedText, new ArrayList<String>(this.sens))).getConsented();
        return MarketplaceSharedConsentDto.builder().shouldCollectConsent(false).build();
    }

    private boolean shouldCollectConsent() {
        if (this.sens.isEmpty()) {
            log.debug("Skipping consent collection as there is no SEN registered to the product");
            return false;
        }
        Optional<String> cloudSiteId = this.getLinkedCloudSiteId();
        return cloudSiteId.isPresent() && !this.hasConsentOnCloud(cloudSiteId.get(), this.sens.first());
    }

    private synchronized boolean hasConsentOnCloud(String cloudSiteId, String sen) {
        if (this.isConsentedOnCloud) {
            return true;
        }
        this.isConsentedOnCloud = this.appMigrationServiceClient.getConsent(cloudSiteId, "allowAppMigrationsDataShare", sen).getConsented();
        return this.isConsentedOnCloud;
    }

    private Optional<String> getLinkedCloudSiteId() {
        return this.cloudSiteService.getNonFailingToken().flatMap(token -> this.cloudSiteService.getByContainerToken((String)token).map(CloudSite::getCloudId));
    }
}

