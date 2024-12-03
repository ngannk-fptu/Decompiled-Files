/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.plugins.rest.entities.SearchResultEntityList
 *  com.atlassian.confluence.plugins.rest.entities.builders.PersonalInformationContentEntityBuilder
 *  com.atlassian.confluence.plugins.rest.manager.DateEntityFactory
 *  com.atlassian.confluence.plugins.rest.manager.UserEntityHelper
 *  com.atlassian.confluence.plugins.rest.resources.AbstractResource
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.avatar.AvatarProviderAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.mentions.rest;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntityList;
import com.atlassian.confluence.plugins.rest.entities.builders.PersonalInformationContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.avatar.AvatarProviderAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/network")
public class NetworkResource
extends AbstractResource {
    private final PermissionManager permissionManager;
    private final PersonalInformationManager personalInformationManager;
    private final UserAccessor userAccessor;
    private final SettingsManager settingsManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final DateEntityFactory dateEntityFactory;
    private final NetworkService networkService;
    private final AvatarProviderAccessor avatarProviderAccessor;
    private final AttachmentManager attachmentManager;

    public NetworkResource(PermissionManager permissionManager, UserAccessor userAccessor, PersonalInformationManager personalInformationManager, SettingsManager settingsManager, I18NBeanFactory i18NBeanFactory, SpacePermissionManager spacePermissionManager, WebResourceUrlProvider webResourceUrlProvider, DateEntityFactory dateEntityFactory, NetworkService networkService, AvatarProviderAccessor avatarProviderAccessor, AttachmentManager attachmentManager) {
        super(userAccessor, spacePermissionManager);
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.personalInformationManager = personalInformationManager;
        this.settingsManager = settingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.dateEntityFactory = dateEntityFactory;
        this.networkService = networkService;
        this.avatarProviderAccessor = avatarProviderAccessor;
        this.attachmentManager = attachmentManager;
    }

    @GET
    @Produces(value={"application/json"})
    public Response doSearch(@QueryParam(value="max-results") @DefaultValue(value="10") Integer maxResults) {
        this.createRequestContext();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (currentUser == null || !this.permissionManager.hasPermission((com.atlassian.user.User)currentUser, Permission.VIEW, PermissionManager.TARGET_PEOPLE_DIRECTORY)) {
            return Response.noContent().build();
        }
        ImmutableSet items = new LinkedHashSet();
        items.add(User.fromUsername((String)currentUser.getName()));
        items.addAll(this.networkService.getFollowing(currentUser.getKey(), (PageRequest)new SimplePageRequest(0, maxResults.intValue())).getResults());
        if (items.size() < maxResults) {
            Iterator followers = this.networkService.getFollowers(currentUser.getKey(), (PageRequest)new SimplePageRequest(0, maxResults.intValue())).iterator();
            while (items.size() < maxResults && followers.hasNext()) {
                items.add((User)followers.next());
            }
        }
        if (items.size() > maxResults) {
            items = ImmutableSet.copyOf((Iterable)Iterables.limit(items, (int)maxResults));
        }
        PersonalInformationContentEntityBuilder entityBuilder = new PersonalInformationContentEntityBuilder(this.settingsManager, this.dateEntityFactory, this.userAccessor, new UserEntityHelper(this.userAccessor, this.settingsManager, this.webResourceUrlProvider, this.i18NBeanFactory), this.avatarProviderAccessor, this.attachmentManager);
        List resultList = items.stream().map(item -> this.userAccessor.getUserByName(item.getUsername())).map(item -> {
            PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation((com.atlassian.user.User)item);
            return entityBuilder.build(personalInformation);
        }).collect(Collectors.toList());
        SearchResultEntityList searchResultEntityList = new SearchResultEntityList();
        searchResultEntityList.setResults(resultList);
        searchResultEntityList.setTotalSize(resultList.size());
        return Response.ok((Object)searchResultEntityList).build();
    }
}

