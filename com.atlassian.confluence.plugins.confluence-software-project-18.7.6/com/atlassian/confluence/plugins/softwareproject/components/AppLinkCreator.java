/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.core.InternalTypeAccessor
 *  com.atlassian.applinks.core.rest.PermissionResource
 *  com.atlassian.applinks.core.rest.model.PermissionCodeEntity
 *  com.atlassian.applinks.core.rest.permission.PermissionCode
 *  com.atlassian.applinks.core.rest.util.RestUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.link.EntityLinkBuilderFactory
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.confluence.plugins.ia.service.SidebarLinkService
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.softwareproject.components;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.PermissionResource;
import com.atlassian.applinks.core.rest.model.PermissionCodeEntity;
import com.atlassian.applinks.core.rest.permission.PermissionCode;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.link.EntityLinkBuilderFactory;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Throwables;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AppLinkCreator {
    private static final Logger log = LoggerFactory.getLogger(AppLinkCreator.class);
    private static final String LOCAL_LINK_TYPE_ID = "com.atlassian.applinks.api.application.confluence.ConfluenceSpaceEntityType";
    private static final String REMOTE_LINK_TYPE_ID = "jira.project";
    private static final String JIRA_PROJECT_ICON_CLASS = "jira-project";
    private final MutatingEntityLinkService entityLinkService;
    private final InternalTypeAccessor typeAccessor;
    private final EntityLinkBuilderFactory entityLinkFactory;
    private final ApplicationLinkService applicationLinkService;
    private final SidebarLinkService sidebarLinkService;
    private final RestUrlBuilder restUrlBuilder;
    private final InternalHostApplication internalHostApplication;

    @Autowired
    public AppLinkCreator(MutatingEntityLinkService entityLinkService, InternalTypeAccessor typeAccessor, EntityLinkBuilderFactory entityLinkFactory, ApplicationLinkService applicationLinkService, SidebarLinkService sidebarLinkService, RestUrlBuilder restUrlBuilder, InternalHostApplication internalHostApplication) {
        this.entityLinkService = Objects.requireNonNull(entityLinkService);
        this.typeAccessor = Objects.requireNonNull(typeAccessor);
        this.entityLinkFactory = Objects.requireNonNull(entityLinkFactory);
        this.applicationLinkService = Objects.requireNonNull(applicationLinkService);
        this.sidebarLinkService = Objects.requireNonNull(sidebarLinkService);
        this.restUrlBuilder = Objects.requireNonNull(restUrlBuilder);
        this.internalHostApplication = Objects.requireNonNull(internalHostApplication);
    }

    public EntityLink addJiraAppLink(Space space, Map<String, Object> context) {
        try {
            EntityLink jiraLink = this.createEntityLink(space, context);
            this.sidebarLinkService.create(space.getKey(), null, jiraLink.getName(), jiraLink.getDisplayUrl().toString(), JIRA_PROJECT_ICON_CLASS);
        }
        catch (Exception e) {
            Throwables.propagate((Throwable)e);
        }
        return null;
    }

    private EntityLink createEntityLink(Space space, Map<String, Object> context) throws Exception {
        String projectName;
        ApplicationLink applicationLink = this.getJiraApplicationLink((String)context.get("jira-server-id"));
        if (applicationLink == null) {
            return null;
        }
        String projectKey = (String)context.get("project-key");
        if (StringUtils.isBlank((String)projectKey)) {
            projectKey = space.getKey();
        }
        if (StringUtils.isBlank((String)(projectName = (String)context.get("project-name")))) {
            projectName = space.getName();
        }
        EntityType remoteType = this.typeAccessor.loadEntityType(REMOTE_LINK_TYPE_ID);
        EntityLink newLink = this.entityLinkFactory.builder().applicationLink(applicationLink).key(projectKey).name(projectName).type(remoteType).primary(false).build();
        String localKey = space.getKey();
        EntityType localType = this.typeAccessor.loadEntityType(LOCAL_LINK_TYPE_ID);
        if (this.canAddReciprocalLink(applicationLink)) {
            this.entityLinkService.addReciprocatedEntityLink(localKey, localType.getClass(), newLink);
        } else {
            this.entityLinkService.addEntityLink(localKey, localType.getClass(), newLink);
        }
        return newLink;
    }

    private ApplicationLink getJiraApplicationLink(String jiraServerId) throws TypeNotInstalledException {
        ApplicationLink applicationLink;
        block2: {
            ApplicationLink link;
            applicationLink = null;
            if (StringUtils.isNotBlank((String)jiraServerId)) {
                applicationLink = this.applicationLinkService.getApplicationLink(new ApplicationId(jiraServerId));
            }
            if (applicationLink != null) break block2;
            Iterator iterator = this.applicationLinkService.getApplicationLinks(JiraApplicationType.class).iterator();
            while (iterator.hasNext() && !(applicationLink = (link = (ApplicationLink)iterator.next())).isPrimary()) {
            }
        }
        return applicationLink;
    }

    private boolean canAddReciprocalLink(ApplicationLink applicationLink) {
        if (applicationLink == null) {
            return false;
        }
        ApplicationLinkRequestFactory authenticatedRequestFactory = applicationLink.createAuthenticatedRequestFactory();
        URI restBase = RestUtil.getBaseRestUri((ApplicationLink)applicationLink);
        ApplicationId thisApplicationId = this.internalHostApplication.getId();
        String url = ((PermissionResource)this.restUrlBuilder.getUrlFor(restBase, PermissionResource.class)).canCreateEntityLink(thisApplicationId).toString();
        try {
            PermissionCode permissionState = (PermissionCode)authenticatedRequestFactory.createRequest(Request.MethodType.GET, url).executeAndReturn(response -> {
                if (response.getStatusCode() == 200) {
                    try {
                        return ((PermissionCodeEntity)response.getEntity(PermissionCodeEntity.class)).getCode();
                    }
                    catch (Exception e) {
                        throw new ResponseException(String.format("Permission check failed, exception encountered processing response: %s", e));
                    }
                }
                if (response.getStatusCode() == 401) {
                    return PermissionCode.AUTHENTICATION_FAILED;
                }
                throw new ResponseException(String.format("Permission check failed, received %s", response.getStatusCode()));
            });
            return permissionState == PermissionCode.ALLOWED;
        }
        catch (CredentialsRequiredException | ResponseException e) {
            log.warn("Unable to check if app link can be reciprocal", e);
            return false;
        }
    }
}

