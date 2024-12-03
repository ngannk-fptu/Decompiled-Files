/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.navlink.consumer.menu.rest;

import com.atlassian.plugins.navlink.consumer.menu.rest.MenuNavigationLinkEntity;
import com.atlassian.plugins.navlink.consumer.menu.services.MenuService;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/")
public class MenuResource {
    private final MenuService menuService;
    private final LocaleResolver localeResolver;
    private final UserManager userManager;

    public MenuResource(MenuService menuService, LocaleResolver localeResolver, UserManager userManager) {
        this.menuService = menuService;
        this.localeResolver = localeResolver;
        this.userManager = userManager;
    }

    @GET
    @Path(value="{key}")
    @Produces(value={"application/json"})
    @AnonymousSiteAccess
    public Response getMenuByKey(@Context HttpServletRequest hsr, @PathParam(value="key") String key) {
        Iterable<NavigationLink> menuNavigationLinks = this.menuService.getMenuItems(key, this.userManager.getRemoteUsername(hsr), this.localeResolver.getLocale());
        return this.createMenuNavigationLinkResponse(menuNavigationLinks);
    }

    @GET
    @Path(value="appswitcher")
    @Produces(value={"application/json"})
    @AnonymousSiteAccess
    public Response getAppSwitcherMenu(@Context HttpServletRequest hsr) {
        Iterable<NavigationLink> menuNavigationLinks = this.menuService.getAppSwitcherItems(this.userManager.getRemoteUsername(hsr));
        return this.createMenuNavigationLinkResponse(menuNavigationLinks);
    }

    private Response createMenuNavigationLinkResponse(Iterable<NavigationLink> menuNavigationLinks) {
        Iterable menuEntities = Iterables.transform(menuNavigationLinks, this.convertToEntities());
        return Response.ok((Object)menuEntities).build();
    }

    @Nonnull
    private Function<NavigationLink, MenuNavigationLinkEntity> convertToEntities() {
        return new Function<NavigationLink, MenuNavigationLinkEntity>(){

            public MenuNavigationLinkEntity apply(@Nullable NavigationLink menuNavigationLink) {
                return menuNavigationLink != null ? new MenuNavigationLinkEntity(menuNavigationLink) : null;
            }
        };
    }

    @PUT
    @Path(value="userdata")
    @Produces(value={"application/json"})
    public Response setUserData(@Context HttpServletRequest hsr, StorageBean storageBean) {
        this.menuService.setUserData(storageBean.key, storageBean.value);
        return Response.ok((Object)storageBean).build();
    }

    @GET
    @Path(value="userdata")
    @Produces(value={"application/json"})
    public Response getUserData(@Context HttpServletRequest hsr, @QueryParam(value="key") String key) {
        return Response.ok((Object)new StorageBean(key, this.menuService.getUserData(key))).build();
    }

    private CacheControl cacheFor(int duration, TimeUnit unit, boolean isPrivate) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge((int)unit.toSeconds(duration));
        cacheControl.setPrivate(isPrivate);
        return cacheControl;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class StorageBean {
        @JsonProperty
        String key;
        @JsonProperty
        String value;

        public StorageBean() {
        }

        public StorageBean(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}

