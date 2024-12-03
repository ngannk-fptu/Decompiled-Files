/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceSpaceEntityType
 *  com.atlassian.applinks.host.spi.AbstractInternalHostApplication
 *  com.atlassian.applinks.host.spi.DefaultEntityReference
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.spi.application.ApplicationIdUtil
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.applinks;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceSpaceEntityType;
import com.atlassian.applinks.host.spi.AbstractInternalHostApplication;
import com.atlassian.applinks.host.spi.DefaultEntityReference;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.spi.application.ApplicationIdUtil;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceInternalHostApplication
extends AbstractInternalHostApplication {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceInternalHostApplication.class);
    private final SettingsManager settingsManager;
    private final ConfluenceSidManager confluenceSidManager;
    private final TypeAccessor typeAccessor;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public ConfluenceInternalHostApplication(PluginAccessor pluginAccessor, SettingsManager settingsManager, ConfluenceSidManager confluenceSidManager, TypeAccessor typeAccessor, SpaceManager spaceManager, PermissionManager permissionManager, WebResourceUrlProvider webResourceUrlProvider) {
        super((PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor));
        this.settingsManager = (SettingsManager)Preconditions.checkNotNull((Object)settingsManager);
        this.confluenceSidManager = (ConfluenceSidManager)Preconditions.checkNotNull((Object)confluenceSidManager);
        this.typeAccessor = (TypeAccessor)Preconditions.checkNotNull((Object)typeAccessor);
        this.spaceManager = (SpaceManager)Preconditions.checkNotNull((Object)spaceManager);
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager);
        this.webResourceUrlProvider = (WebResourceUrlProvider)Preconditions.checkNotNull((Object)webResourceUrlProvider);
    }

    public URI getBaseUrl() {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        try {
            return new URI(StringUtils.stripEnd((String)baseUrl, (String)"/"));
        }
        catch (URISyntaxException use) {
            throw new NotValidException("Invalid base URL: " + baseUrl);
        }
    }

    public URI getIconUrl() {
        String url = this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.ABSOLUTE) + "/images/logo/confluence_16_white.png";
        return URI.create(url);
    }

    public URI getDocumentationBaseUrl() {
        return URI.create("http://confluence.atlassian.com/display/APPLINKS");
    }

    public String getName() {
        return this.settingsManager.getGlobalSettings().getSiteTitle();
    }

    public ApplicationType getType() {
        return this.typeAccessor.getApplicationType(ConfluenceApplicationType.class);
    }

    public Iterable<EntityReference> getLocalEntities() {
        User user = this.resolveCurrentUser();
        SpacesQuery query = SpacesQuery.newQuery().forUser(user).build();
        return Iterables.transform(this.spaceManager.getAllSpaces(query), space -> new DefaultEntityReference(space.getKey(), space.getName(), (EntityType)Preconditions.checkNotNull((Object)((ConfluenceSpaceEntityType)this.typeAccessor.getEntityType(ConfluenceSpaceEntityType.class)), (Object)"Couldn't load ConfluenceSpaceEntityType")));
    }

    public boolean doesEntityExist(String key, Class<? extends EntityType> type) {
        User user = this.resolveCurrentUser();
        Space space = this.spaceManager.getSpace(key);
        return ConfluenceSpaceEntityType.class.isAssignableFrom(type) && space != null && this.permissionManager.hasPermission(user, Permission.VIEW, space);
    }

    public boolean doesEntityExistNoPermissionCheck(String key, Class<? extends EntityType> type) {
        Space space = this.spaceManager.getSpace(key);
        return ConfluenceSpaceEntityType.class.isAssignableFrom(type) && space != null;
    }

    public EntityReference toEntityReference(Object domainObject) {
        Preconditions.checkArgument((boolean)(domainObject instanceof Space), (Object)"'Space' is the only domain Object supported by Confluence");
        Space space = (Space)domainObject;
        return this.createSpaceEntityReference(space, ConfluenceSpaceEntityType.class);
    }

    public EntityReference toEntityReference(String key, Class<? extends EntityType> type) {
        Space space = (Space)Preconditions.checkNotNull((Object)this.spaceManager.getSpace(key));
        return this.createSpaceEntityReference(space, type);
    }

    public boolean canManageEntityLinksFor(EntityReference entityReference) {
        User user = this.resolveCurrentUser();
        Space space = this.spaceManager.getSpace(entityReference.getKey());
        return null != user && null != space && (this.permissionManager.isConfluenceAdministrator(user) || this.permissionManager.hasPermission(user, Permission.ADMINISTER, space));
    }

    public ApplicationId getId() {
        try {
            byte[] bytes;
            try {
                bytes = this.confluenceSidManager.getSid().getBytes(this.settingsManager.getGlobalSettings().getDefaultEncoding());
            }
            catch (UnsupportedEncodingException e) {
                try {
                    bytes = this.confluenceSidManager.getSid().getBytes("UTF-8");
                }
                catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("UTF-8 encoding not supported?", uee);
                }
            }
            return new ApplicationId(UUID.nameUUIDFromBytes(bytes).toString());
        }
        catch (ConfigurationException e) {
            log.warn("Unable to resolve the Confluence SID: {}", (Object)e.getLocalizedMessage(), (Object)e);
            return ApplicationIdUtil.generate((URI)this.getBaseUrl());
        }
    }

    User resolveCurrentUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private EntityReference createSpaceEntityReference(Space space, Class<? extends EntityType> type) {
        EntityType entityType = (EntityType)Preconditions.checkNotNull((Object)this.typeAccessor.getEntityType(type));
        return new DefaultEntityReference(space.getKey(), space.getName(), entityType);
    }

    public boolean hasPublicSignup() {
        return !this.settingsManager.getGlobalSettings().isDenyPublicSignup();
    }
}

