/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.TimePeriod
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.sun.jersey.api.NotFoundException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.featurediscovery.rest;

import com.atlassian.confluence.plugins.featurediscovery.rest.entity.FeatureItem;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.TimePeriod;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.sun.jersey.api.NotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@AnonymousSiteAccess
@Path(value="/")
public class FeatureDiscoveryResource {
    private final FeatureDiscoveryService featureDiscoveryService;
    private final PluginAccessor pluginAccessor;

    public FeatureDiscoveryResource(FeatureDiscoveryService featureDiscoveryService, PluginAccessor pluginAccessor) {
        this.featureDiscoveryService = featureDiscoveryService;
        this.pluginAccessor = pluginAccessor;
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/{context}/{key}")
    public Response isNew(@PathParam(value="context") String context, @PathParam(value="key") String key, @QueryParam(value="newPeriod") Long newPeriod) {
        boolean isNew = newPeriod == null ? this.featureDiscoveryService.isNew(context, key) : this.featureDiscoveryService.isNew(context, key, new TimePeriod(newPeriod.longValue(), TimeUnit.SECONDS));
        return Response.ok((Object)new FeatureItem(context, key, isNew)).build();
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="/new")
    public Response getNew(List<FeatureItem> features, @QueryParam(value="newPeriod") Long newPeriod) {
        List<ModuleCompleteKey> moduleCompleteKeys = this.getModuleCompleteKeys(features);
        List<ModuleCompleteKey> newModuleCompleteKeys = newPeriod == null ? this.featureDiscoveryService.getNew(moduleCompleteKeys) : this.featureDiscoveryService.getNew(moduleCompleteKeys, new TimePeriod(newPeriod.longValue(), TimeUnit.SECONDS));
        return Response.ok(this.getPluginModules(newModuleCompleteKeys)).build();
    }

    private List<ModuleCompleteKey> getModuleCompleteKeys(List<FeatureItem> pluginModules) {
        return pluginModules.stream().map(pluginModule -> new ModuleCompleteKey(pluginModule.getContext(), pluginModule.getKey())).collect(Collectors.toList());
    }

    private List<FeatureItem> getPluginModules(List<ModuleCompleteKey> newModuleCompleteKeys) {
        return newModuleCompleteKeys.stream().map(moduleCompleteKey -> new FeatureItem(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey(), true)).collect(Collectors.toList());
    }

    @GET
    @Path(value="/discovered")
    @Produces(value={"application/json"})
    public Response getDiscoveredPluginFeatures() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return Response.ok().entity(this.featureDiscoveryService.getFeaturesDiscoveredByUser(user)).build();
    }

    @POST
    @Path(value="/discovered/{pluginKey}/{featureKey}")
    public Response markPluginFeatureDiscovered(@PathParam(value="pluginKey") String pluginKey, @PathParam(value="featureKey") String featureKey) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
        if (plugin == null) {
            throw new NotFoundException();
        }
        this.featureDiscoveryService.forPlugin(plugin).markDiscovered(user, featureKey);
        return Response.ok().build();
    }
}

