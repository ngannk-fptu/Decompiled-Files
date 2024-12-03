/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.mma.service;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.mma.model.ServerInstance;
import com.atlassian.migration.agent.mma.model.ServerInstanceCreateResponse;
import com.atlassian.migration.agent.mma.model.SpaceMetadata;
import com.atlassian.migration.agent.mma.model.SpaceMetadataDTO;
import com.atlassian.migration.agent.mma.model.processor.MetadataBatch;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.impl.SpaceStore;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class MigrationMetadataAggregatorService {
    private final SystemInformationService sysInfoService;
    private final PluginVersionManager pluginVersionManager;
    private final LicenseHandler licenseHandler;
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final SpaceStore spaceStore;
    private final CloudSiteStore cloudSiteStore;
    private static final Logger log = ContextLoggerFactory.getLogger(MigrationMetadataAggregatorService.class);
    private static final String CONFLUENCE_SERVER_PRODUCT_KEY = "confluence-server";
    static final int BATCH_SIZE_SPACE_METADATA = 1000;

    public MigrationMetadataAggregatorService(SystemInformationService sysInfoService, PluginVersionManager pluginVersionManager, LicenseHandler licenseHandler, EnterpriseGatekeeperClient enterpriseGatekeeperClient, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SpaceStore spaceStore, CloudSiteStore cloudSiteStore) {
        this.sysInfoService = sysInfoService;
        this.pluginVersionManager = pluginVersionManager;
        this.licenseHandler = licenseHandler;
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.spaceStore = spaceStore;
        this.cloudSiteStore = cloudSiteStore;
    }

    public ServerInstanceCreateResponse createServerInstanceCloudSitePairInMMA(String containerToken, String cloudId) {
        if (!this.migrationDarkFeaturesManager.isCloudFirstMigrationEnabled()) {
            return null;
        }
        MetadataBatch<ServerInstance> serverInstanceDTO = new MetadataBatch<ServerInstance>(this.mapServerInstanceDto(cloudId));
        ServerInstanceCreateResponse response = this.enterpriseGatekeeperClient.sendServerInstanceInfoToMigrationMetadataAggregator(containerToken, cloudId, serverInstanceDTO);
        log.info("Sent request to MMA for Server Instance\n[{}]", serverInstanceDTO);
        return response;
    }

    private ServerInstance mapServerInstanceDto(String cloudId) {
        return ServerInstance.builder().cloudId(cloudId).serverId(this.sysInfoService.getConfluenceInfo().getServerId()).supportEntitlementNumber(this.sysInfoService.getConfluenceInfo().getSupportEntitlementNumber()).productVersion(this.sysInfoService.getConfluenceInfo().getVersion()).serverBaseUrl(this.sysInfoService.getConfluenceInfo().getBaseUrl()).cmaVersion(this.pluginVersionManager.getPluginVersion()).organisationName(this.getOrganisationName()).productKey(CONFLUENCE_SERVER_PRODUCT_KEY).build();
    }

    private String getOrganisationName() {
        return this.licenseHandler.getAllProductLicenses().stream().map(BaseLicenseDetails::getOrganisationName).findFirst().orElse(null);
    }

    public void sendSpaceMetadataToMMAForAllCloudSites() {
        if (!this.migrationDarkFeaturesManager.isCloudFirstMigrationEnabled()) {
            return;
        }
        List<CloudSite> nonFailingCloudSites = this.cloudSiteStore.getNonFailingSites();
        nonFailingCloudSites.forEach(site -> this.sendSpaceMetadataToMMA(site.getContainerToken(), site.getCloudId()));
    }

    public List<JsonElement> sendSpaceMetadataToMMA(String containerToken, String cloudId) {
        if (!this.migrationDarkFeaturesManager.isCloudFirstMigrationEnabled()) {
            return null;
        }
        List spaceMetadata = this.spaceStore.getSpaceMetadata().stream().map(s -> new SpaceMetadataDTO((SpaceMetadata)s, cloudId)).collect(Collectors.toList());
        List<JsonElement> responses = Lists.partition(spaceMetadata, (int)1000).stream().map(spaceMetadataChunk -> {
            try {
                return this.enterpriseGatekeeperClient.sendSpaceMetadataToMigrationMetadataAggregator(containerToken, cloudId, new MetadataBatch<SpaceMetadataDTO>((List<SpaceMetadataDTO>)spaceMetadataChunk));
            }
            catch (Exception e) {
                log.error("There was an error sending a batch of metadata to MMA: {}", (Object)e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        log.info("Emitted latest Space metadata to MMA. Sent [{}] spaces in [{}] successful batches", (Object)spaceMetadata.size(), (Object)responses.size());
        return responses;
    }
}

