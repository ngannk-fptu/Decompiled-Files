/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.application.confluence.ConfluenceSpaceEntityTypeImpl
 *  com.atlassian.applinks.application.jira.JiraProjectEntityTypeImpl
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.conluenceview.services.impl;

import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.application.confluence.ConfluenceSpaceEntityTypeImpl;
import com.atlassian.applinks.application.jira.JiraProjectEntityTypeImpl;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.LinkedSpaceDto;
import com.atlassian.confluence.plugins.conluenceview.rest.exception.InvalidRequestException;
import com.atlassian.confluence.plugins.conluenceview.services.ConfluenceJiraLinksService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DefaultConfluenceJiraLinksService
implements ConfluenceJiraLinksService {
    private static final int MAX_SPACES = 100;
    private final MutatingEntityLinkService entityLinkService;
    private final InternalHostApplication applinkHostApplication;
    private final HostApplication hostApplication;
    private final ReadOnlyApplicationLinkService appLinkService;
    private final SpaceService spaceService;

    public DefaultConfluenceJiraLinksService(MutatingEntityLinkService entityLinkService, InternalHostApplication applinkHostApplication, HostApplication hostApplication, ReadOnlyApplicationLinkService appLinkService, SpaceService spaceService) {
        this.entityLinkService = entityLinkService;
        this.applinkHostApplication = applinkHostApplication;
        this.hostApplication = hostApplication;
        this.appLinkService = appLinkService;
        this.spaceService = spaceService;
    }

    @Override
    public String getODApplicationLinkId() {
        return this.hostApplication.getId().get();
    }

    @Override
    public List<LinkedSpaceDto> getLinkedSpaces(String jiraUrl, String projectKey) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            throw new RuntimeException("User is not authenticated");
        }
        if (StringUtils.isBlank((CharSequence)jiraUrl) || StringUtils.isBlank((CharSequence)projectKey)) {
            throw new InvalidRequestException("Jira url and project key cannot be empty");
        }
        ReadOnlyApplicationLink appLink = this.getAppLink(jiraUrl);
        if (appLink == null) {
            return Collections.emptyList();
        }
        ArrayList<String> spaceKeys = new ArrayList<String>();
        Iterable localEntities = this.applinkHostApplication.getLocalEntities();
        for (EntityReference localEntity : localEntities) {
            if (!this.hasLinkToProject(localEntity, projectKey)) continue;
            spaceKeys.add(localEntity.getKey());
        }
        ArrayList<LinkedSpaceDto> spaceDtos = new ArrayList<LinkedSpaceDto>();
        if (spaceKeys.size() > 0) {
            PageResponse spaces = this.spaceService.find(new Expansion[]{new Expansion("icon")}).withKeys(spaceKeys.toArray(new String[0])).fetchMany((PageRequest)new SimplePageRequest(0, 100));
            for (Space space : spaces) {
                spaceDtos.add(LinkedSpaceDto.newBuilder().withSpaceKey(space.getKey()).withSpaceName(space.getName()).withSpaceUrl("/display/" + space.getKey()).withSpaceIcon(((Icon)space.getIconRef().get()).getPath()).build());
            }
        }
        return spaceDtos;
    }

    private boolean hasLinkToProject(EntityReference entity, String projectKey) {
        if (entity.getType().getClass() == ConfluenceSpaceEntityTypeImpl.class) {
            Iterable links = this.entityLinkService.getEntityLinksForKey(entity.getKey(), ConfluenceSpaceEntityTypeImpl.class, JiraProjectEntityTypeImpl.class);
            for (EntityLink link : links) {
                if (!link.getKey().equals(projectKey)) continue;
                return true;
            }
        }
        return false;
    }

    private ReadOnlyApplicationLink getAppLink(String jiraUrl) {
        Iterable appLinks = this.appLinkService.getApplicationLinks(JiraApplicationType.class);
        if (appLinks == null) {
            return null;
        }
        for (ReadOnlyApplicationLink appLink : appLinks) {
            if (!appLink.getDisplayUrl().toString().equals(jiraUrl)) continue;
            return appLink;
        }
        return null;
    }
}

