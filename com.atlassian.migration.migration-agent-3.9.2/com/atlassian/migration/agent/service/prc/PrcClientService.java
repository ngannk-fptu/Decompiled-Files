/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.migration.prc.client.PollerExecutionService
 *  com.atlassian.migration.prc.client.PrcPollerManager
 *  com.atlassian.migration.prc.client.model.PollerConfig
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.prc;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.mapi.external.model.PublicApiException;
import com.atlassian.migration.agent.rest.ContainerTokenState;
import com.atlassian.migration.agent.rest.ContainerTokenValidator;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.prc.PollerConfigHandler;
import com.atlassian.migration.agent.service.prc.PrcPollerExecutionService;
import com.atlassian.migration.agent.service.prc.PrcPollerMetadataCache;
import com.atlassian.migration.prc.client.PollerExecutionService;
import com.atlassian.migration.prc.client.PrcPollerManager;
import com.atlassian.migration.prc.client.model.PollerConfig;
import com.atlassian.sal.api.license.LicenseHandler;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrcClientService {
    private final PollerConfigHandler pollerConfigHandler;
    private final LicenseHandler licenseHandler;
    private final CloudSiteService cloudSiteService;
    private final ContainerTokenValidator containerTokenValidator;
    private final PrcPollerManager prcPollerManager;
    private final PrcPollerMetadataCache prcPollerMetadataCache;
    private static final String CONFLUENCE_FLOW = "confluence-server-to-cloud-";
    private static final Logger log = LoggerFactory.getLogger(PrcClientService.class);

    public PrcClientService(PollerConfigHandler pollerConfigHandler, LicenseHandler licenseHandler, CloudSiteService cloudSiteService, PrcPollerExecutionService prcPollerExecutionService, ContainerTokenValidator containerTokenValidator, PrcPollerMetadataCache prcPollerMetadataCache) {
        this.pollerConfigHandler = pollerConfigHandler;
        this.licenseHandler = licenseHandler;
        this.cloudSiteService = cloudSiteService;
        this.prcPollerManager = new PrcPollerManager((PollerExecutionService)prcPollerExecutionService);
        this.containerTokenValidator = containerTokenValidator;
        this.prcPollerMetadataCache = prcPollerMetadataCache;
    }

    public Instant attach(String cloudUrl) {
        Optional<CloudSite> cloudSiteOpt = this.cloudSiteService.getByCloudUrl(cloudUrl);
        if (!cloudSiteOpt.isPresent() || this.containerTokenValidator.validateContainerToken(cloudSiteOpt.get()) != ContainerTokenState.VALID) {
            log.error("Cloud site is either incorrect or not authorised with server for cloud url : {}", (Object)cloudUrl);
            throw new PublicApiException.CloudUrlDoesNotExist(cloudUrl);
        }
        try {
            String channelName = CONFLUENCE_FLOW + this.licenseHandler.getServerId();
            PollerConfig pollerConfig = this.pollerConfigHandler.getPollerConfigWithCallbacks(channelName, cloudSiteOpt.get().getCloudId(), cloudSiteOpt.get().getContainerToken());
            this.prcPollerMetadataCache.setPrcPollerUserContext(cloudUrl, AuthenticatedUserThreadLocal.get());
            this.prcPollerMetadataCache.removeContainerTokenInCacheForCloudId(cloudSiteOpt.get().getCloudId());
            log.info("Starting poller for cloudUrl : {}", (Object)cloudUrl);
            this.prcPollerManager.startPollingAsync(pollerConfig);
            return Instant.ofEpochSecond(Instant.now().getEpochSecond() + (long)this.pollerConfigHandler.getPollerExpiryTimeInSec().intValue());
        }
        catch (Exception ex) {
            log.error("Error starting poller for cloud url : {} with error: {}", (Object)cloudUrl, (Object)ex);
            throw ex;
        }
    }
}

