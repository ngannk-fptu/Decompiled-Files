/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.message.LocaleResolver
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.upm.ProductUpdatePluginCompatibility;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/product-updates/{build-number}/compatibility")
public class ProductUpdatePluginCompatibilityResource {
    private final UpmRepresentationFactory representationFactory;
    private final LocaleResolver localeResolver;
    private final PacClient client;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginMetadataAccessor metadata;
    private final PluginRetriever pluginRetriever;
    private final ApplicationPluginsManager applicationPluginsManager;

    public ProductUpdatePluginCompatibilityResource(UpmRepresentationFactory factory, LocaleResolver localeResolver, PacClient client, PermissionEnforcer permissionEnforcer, PluginMetadataAccessor metadata, PluginRetriever pluginRetriever, ApplicationPluginsManager applicationPluginsManager) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.localeResolver = Objects.requireNonNull(localeResolver, "localeResolver");
        this.representationFactory = Objects.requireNonNull(factory, "representationFactory");
        this.client = Objects.requireNonNull(client, "client");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "licensingUsageVerifier");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.compatibility+json"})
    public Response get(@PathParam(value="build-number") String buildNumber, @Context HttpServletRequest request) throws MpacException {
        this.permissionEnforcer.enforcePermission(Permission.GET_PRODUCT_UPDATE_COMPATIBILITY);
        int buildNumberInt = Integer.parseInt(buildNumber);
        List allPlugins = StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).collect(Collectors.toList());
        List<Plugin> collect = allPlugins.stream().map(com.atlassian.upm.core.Plugin::getPlugin).collect(Collectors.toList());
        Set<String> applicationPluginKeys = this.applicationPluginsManager.getApplicationRelatedPlugins(collect).keySet();
        List<com.atlassian.upm.core.Plugin> filteredPlugins = allPlugins.stream().filter(Plugins.userInstalled(this.metadata)).filter(a -> !applicationPluginKeys.contains(a.getKey())).collect(Collectors.toList());
        ProductUpdatePluginCompatibility pluginCompatibility = this.client.getProductUpdatePluginCompatibility(filteredPlugins, buildNumberInt);
        return Response.ok((Object)this.representationFactory.createProductUpdatePluginCompatibilityRepresentation(pluginCompatibility, buildNumberInt, this.localeResolver.getLocale(request))).build();
    }
}

