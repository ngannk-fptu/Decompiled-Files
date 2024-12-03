/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.confluence;

import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.media.MediaClientToken;
import com.atlassian.migration.agent.media.MediaConfigToken;
import com.atlassian.migration.agent.service.ConfluenceImportExportTaskStatus;
import com.atlassian.migration.agent.service.GlobalEntitiesImportContextDto;
import com.atlassian.migration.agent.service.NonSpaceTemplateConflictsInfo;
import com.atlassian.migration.agent.service.SpaceConflict;
import com.atlassian.migration.agent.service.SpaceImportContextDto;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.catalogue.Space;
import com.atlassian.migration.agent.service.catalogue.model.CloudPageTemplate;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.confluence.AbstractNonSpaceTemplateConflictFinder;
import com.atlassian.migration.agent.service.confluence.EditedSystemTemplateConflictFinder;
import com.atlassian.migration.agent.service.confluence.GlobalPageTemplateConflictFinder;
import com.atlassian.migration.agent.service.confluence.exception.ConfluenceCloudServiceException;
import com.atlassian.migration.agent.service.confluence.request.BulkSpaceImportStatusResponse;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class ConfluenceCloudService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ConfluenceCloudService.class);
    private static final AbstractNonSpaceTemplateConflictFinder DEFAULT_NON_SPACE_TEMPLATE_CONFLICT_FINDER_CHAIN = new GlobalPageTemplateConflictFinder(GlobalEntityType.GLOBAL_TEMPLATES, new EditedSystemTemplateConflictFinder(GlobalEntityType.SYSTEM_TEMPLATES));
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;
    private final CloudSiteService cloudSiteService;
    private final PageTemplateManager pageTemplateManager;

    public ConfluenceCloudService(EnterpriseGatekeeperClient enterpriseGatekeeperClient, CloudSiteService cloudSiteService, PageTemplateManager pageTemplateManager) {
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
        this.cloudSiteService = cloudSiteService;
        this.pageTemplateManager = pageTemplateManager;
    }

    public ConfluenceImportExportTaskStatus initiateConfluenceSpaceImport(String cloudId, String containerToken, SpaceImportContextDto spaceImportContext) {
        try {
            return this.enterpriseGatekeeperClient.initiateConfluenceSpaceImport(cloudId, containerToken, spaceImportContext);
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error initiating space import for cloudId: {}", (Object)cloudId, (Object)e);
            throw new ConfluenceCloudServiceException(String.format("Error occurred when initiating space import for cloudId: %s, cause: %s", cloudId, e.getMessage()), e);
        }
    }

    public ConfluenceImportExportTaskStatus getConfluenceSpaceImportProgress(String cloudId, String containerToken, String confTaskId) {
        try {
            return this.enterpriseGatekeeperClient.getConfluenceSpaceImportProgress(cloudId, containerToken, confTaskId);
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error getting space import progress for taskId: {}", (Object)confTaskId, (Object)e);
            throw new ConfluenceCloudServiceException(String.format("Error getting space import progress for taskId: %s, cause: %s", confTaskId, e.getMessage()), e);
        }
    }

    public BulkSpaceImportStatusResponse getBulkConfluenceSpaceImportProgress(String cloudId, String containerToken, List<String> confTaskIds) {
        try {
            return this.enterpriseGatekeeperClient.getBulkConfluenceSpaceImportProgress(cloudId, containerToken, confTaskIds);
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error getting space import progress for taskIds: {}", confTaskIds, (Object)e);
            throw new ConfluenceCloudServiceException(String.format("Error getting bulk space import progress for taskIds: %s, cause: %s", confTaskIds, e.getMessage()), e);
        }
    }

    public MediaClientToken getMediaClientToken(String containerToken, Duration expiryDuration) {
        try {
            log.debug("Requesting Media Token via EG");
            String cloudId = this.cloudSiteService.getByContainerToken(containerToken).map(CloudSite::getCloudId).orElseThrow(() -> new IllegalStateException("Failed to find cloudSite entry for containerToken"));
            MediaConfigToken mediaConfigToken = this.enterpriseGatekeeperClient.getMediaConfigToken(cloudId, containerToken, expiryDuration);
            return new MediaClientToken(mediaConfigToken.getConfig().getClientId(), mediaConfigToken.getToken());
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error getting Media Token", (Throwable)e);
            throw new IllegalStateException("Failed to get media token", e);
        }
    }

    public Set<SpaceConflict> getConflictingSpaces(CloudSite cloudSite, Set<String> spaceKeys) {
        try {
            log.debug("Requesting spaces via EG");
            return this.getConflictingSpacesInCloud(cloudSite, spaceKeys);
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception ex) {
            log.error("Error getting spaces: {}", (Object)ex.getMessage());
            throw new IllegalStateException("Failed to get spaces", ex);
        }
    }

    public Set<SpaceConflict> getConflictingSpacesInCloud(CloudSite cloudSite, Set<String> spaceKeys) {
        Set<Space> allSpaces = this.enterpriseGatekeeperClient.getAllSpaces(cloudSite);
        Set conflictSpaces = allSpaces.stream().filter(space -> spaceKeys.contains(space.key)).collect(Collectors.toSet());
        log.info("SpaceConflictCheck: Found [" + conflictSpaces.size() + "] conflicts from [" + spaceKeys.size() + "] spacesToMigrate");
        return conflictSpaces.stream().map(space -> ConfluenceCloudService.spaceToSpaceConflict(space, cloudSite)).collect(Collectors.toSet());
    }

    private static SpaceConflict spaceToSpaceConflict(Space space, CloudSite site) {
        return new SpaceConflict(space.key, space.name, UriComponentsBuilder.fromUriString((String)site.getCloudUrl()).path("wiki").path(space._links.webui).toUriString());
    }

    public ConfluenceImportExportTaskStatus initiateGlobalEntitiesImport(String cloudId, String containerToken, GlobalEntitiesImportContextDto importContext) {
        try {
            return this.enterpriseGatekeeperClient.initiateGlobalEntitiesImport(cloudId, containerToken, importContext);
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error initiating global templates import for cloudId: {}", (Object)cloudId, (Object)e);
            throw new ConfluenceCloudServiceException(String.format("Error occurred when initiating global templates import for cloudId: %s, cause: %s", cloudId, e.getMessage()), e);
        }
    }

    public ConfluenceImportExportTaskStatus getGlobalEntitiesImportProgress(String cloudId, String containerToken, String taskId) {
        try {
            return this.enterpriseGatekeeperClient.getGlobalEntitiesImportProgress(cloudId, containerToken, taskId);
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error getting global templates import progress for taskId: {}", (Object)taskId, (Object)e);
            throw new ConfluenceCloudServiceException(String.format("Error occurred when getting global templates import progress for taskId: %s, cause: %s", taskId, e.getMessage()), e);
        }
    }

    public NonSpaceTemplateConflictsInfo getNonSpaceTemplateConflictsInfo(GlobalEntityType globalEntityType, String cloudId) {
        List serverPageTemplates = this.pageTemplateManager.getGlobalPageTemplates();
        List<CloudPageTemplate> cloudPageTemplates = this.enterpriseGatekeeperClient.getAllNonSpaceTemplates(cloudId);
        NonSpaceTemplateConflictsInfo conflictsInfo = new NonSpaceTemplateConflictsInfo();
        DEFAULT_NON_SPACE_TEMPLATE_CONFLICT_FINDER_CHAIN.populateNonSpaceConflicts(cloudPageTemplates, serverPageTemplates, conflictsInfo, globalEntityType);
        return conflictsInfo;
    }
}

